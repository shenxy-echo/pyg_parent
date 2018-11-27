package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/23  15:51
 */
@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String,Object > map = new HashMap<>();
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        List<String> categoryList = searchCategoryList(searchMap);//分组查询
        if(!"".equals(searchMap.get("category"))){
            map.putAll(searchBrandAndSpecByCategory((String) searchMap.get("category")));
        }else{
            if(categoryList.size() > 0){
                map.putAll(searchBrandAndSpecByCategory(categoryList.get(0)));
            }
        }
        map.put("categoryList",categoryList);//按分组查询商品类别
        map.putAll(searchList(searchMap));//获取查询结果，并高亮显示
        return map;
    }

    private List<String> searchCategoryList(Map searchMap) {
        List<String> categoryList = new ArrayList<>();
        Query query = new SimpleQuery();
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));//设置查询条件
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions();//设置分组信息
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");//获取分组结果
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();//获取结果中的分组信息
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {//遍历获取groupValue
            String groupValue = tbItemGroupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }


    private Map searchList(Map searchMap){
        Map map =new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        //按照关键字过滤查询结果
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));  //按照关键字查询
        query.addCriteria(criteria);//将查询条件绑定到query上
        //按照商品分类过滤
        if (!"".equals(searchMap.get("category")) ){
            FilterQuery cateFilterQuery = new SimpleFilterQuery();
            Criteria cateCriteria = new Criteria("item_category").is(searchMap.get("category"));
            cateFilterQuery.addCriteria(cateCriteria);
            query.addFilterQuery(cateFilterQuery);
        }
        //按照品牌过滤
        if (!"".equals(searchMap.get("brand")) ){
            FilterQuery brandFilterQuery = new SimpleFilterQuery();
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            brandFilterQuery.addCriteria(brandCriteria);
            query.addFilterQuery(brandFilterQuery);
        }
        //按照规格过滤
        if(searchMap.get("spec")!=null){
            Map<String ,String> spec =(Map<String, String>) searchMap.get("spec");
            for (String s : spec.keySet()) {
                FilterQuery specFilterQuery = new SimpleFilterQuery();
                Criteria specCriteria = new Criteria("item_spec_"+s).is(spec.get(s));
                specFilterQuery.addCriteria(specCriteria);
                query.addFilterQuery(specFilterQuery);
            }
        }
        //按照价格过滤
        if (!"".equals(searchMap.get("price")) ){
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if(!("0".equals(split[0]))){// >=该值
                Criteria lowCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery lowFilter = new SimpleFilterQuery();
                lowFilter.addCriteria(lowCriteria);
                query.addFilterQuery(lowFilter);
            }
            if(!("*".equals(split[1]))){// <=该值
                Criteria highCriteria = new Criteria("item_price").lessThanEqual(split[1]);
                FilterQuery highFilter = new SimpleFilterQuery();
                highFilter.addCriteria(highCriteria);
                query.addFilterQuery(highFilter);
            }
        }

        //排序
        String sortValue = (String)searchMap.get("sort");//排序方式
        String sortField = (String)searchMap.get("sortField");//排序字段
        if(sortValue != null && !"".equals(sortValue)){
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }
        //分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer)searchMap.get("pageSize");
        if(pageNo == null){
            pageNo = 1;
        }
        if (pageSize == null){
            pageSize = 20;
        }

        query.setOffset( (pageNo-1)*pageSize );
        query.setRows(pageSize);

        //获取查询结果
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<TbItem> content = itemHighlightPage.getContent();//查询结果列表
        //设置高亮字段，修改对应字段，得到新的查询结果
        for (HighlightEntry<TbItem> entry : itemHighlightPage.getHighlighted()) {//获取高亮对象 size=10  [738388,741524,816448,858025,.....]
            TbItem tbItem = entry.getEntity();
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();//获取高亮集合 size = 1  获取对象738388
            for (HighlightEntry.Highlight highlight : highlights) {//获取每个高亮对象
                String title = highlight.getSnipplets().get(0);//获取每个对象对应的title  (highlight.getSnipplets())数组的长度为2 存储的是title和brand
                tbItem.setTitle(title);
            }
        }
        map.put("rows",content);
        map.put("total",itemHighlightPage.getTotalElements());
        map.put("totalPages",itemHighlightPage.getTotalPages());
        return map;
    }

    private Map searchBrandAndSpecByCategory(String category){
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null){
            Object brandList = redisTemplate.boundHashOps("brandList").get(typeId);
            Object specList = redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList",brandList);
            map.put("specList",specList);
        }
        return  map;
    }

    @Override
    public void importItemList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    public void deleteByGoodsIds(List goodsIdList){
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
