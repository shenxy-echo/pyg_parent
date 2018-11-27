package com.pyg.sellergoods.service;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/9  0:29
 */
public interface BrandService {
    /**
    * @author 沈小云
    * @description  返回所有的品牌列表
    * @date 2018/10/9 16:24
    * @param
    *@return java.util.List<com.pyg.pojo.TbBrand>
    */
    public List<TbBrand> findAll();

    /**
    * @author 沈小云
    * @description  返回品牌的分页列表
    * @date 2018/10/9 16:25
    * @param
    *@return com.pyg.entity.PageResult
    */
    public PageResult findPage(int pageNum,int pageSize);

    /**
    * @author 沈小云
    * @description  添加品牌
    * @date 2018/10/9 16:49
    * @param
    *@return void
    */
    public void addBrand(TbBrand tbBrand);

    /**
    * @author 沈小云
    * @description  修改动品牌信息
    * @date 2018/10/9 17:32
    * @param
    *@return void
    */
    public void updateBrand(TbBrand tbBrand);

    /**
    * @author 沈小云
    * @description  根据id获取品牌
    * @date 2018/10/9 17:33
    * @param
    *@return com.pyg.pojo.TbBrand
    */
    public TbBrand getBrandById(long id);

    /**
    * @author 沈小云
    * @description  根据id删除品牌
    * @date 2018/10/9 18:57
    * @param 
    *@return void
    */
    public void deleteById(Long[] ids);

    /**
    * @author 沈小云
    * @description  条件搜索
    * @date 2018/10/9 20:09
    * @param
    *@return com.pyg.entity.PageResult
    */
    public PageResult findPage(TbBrand brand, int pageNum,int pageSize);

    /**
    * @author 沈小云
    * @description  获取所有的品牌和id
    * @date 2018/10/11 17:26
    * @param
    *@return java.util.List<java.util.Map>
    */
    public List<Map> selectOptionList();


}
