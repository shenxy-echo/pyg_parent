package com.pyg.page.service.impl;

import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.page.service.ItemPageService;
import com.pyg.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/28  8:54
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void genItemHtml(Long id) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            configuration.setDefaultEncoding("utf-8");
            Template template = configuration.getTemplate("item.ftl");
            Map map = new HashMap();
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
            map.put("goods",goods);//商品
            map.put("goodsDesc",goodsDesc);//商品描述
            //sku集合
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            criteria.andStatusEqualTo("1");
            tbItemExample.setOrderByClause("is_default desc");
            List<TbItem> items = itemMapper.selectByExample(tbItemExample);
            map.put("itemList",items);
            //商品类别
            String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            map.put("category1",category1);
            map.put("category2",category2);
            map.put("category3",category3);

            Writer out = new FileWriter(new File(pageDir+id+".html"));
            template.process(map,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteItemHtml(Long[] ids) {
        try{
            for (Long id : ids) {
                File file = new File(pageDir+id+".html");
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
