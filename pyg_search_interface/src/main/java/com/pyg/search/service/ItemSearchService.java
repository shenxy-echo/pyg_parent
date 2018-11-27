package com.pyg.search.service;

import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/23  15:43
 */
public interface ItemSearchService {
    /**
     * 搜索
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    //批量插入数据到solr中
    public void importItemList(List list);

    //根据id批量删除solr中的数据
    public void deleteByGoodsIds(List goodsIdList);
}
