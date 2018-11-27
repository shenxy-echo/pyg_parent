package com.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.entity.PageResult;
import com.pyg.mapper.TbBrandMapper;
import com.pyg.pojo.TbBrand;
import com.pyg.pojo.TbBrandExample;
import com.pyg.pojo.TbBrandExample.Criteria;
import com.pyg.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/9  0:30
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;
    /* 显示所有品牌列表*/
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

  //* 分页显示所有品牌列表*//*
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*添加品牌*/
    @Override
    public void addBrand(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    /*修改品牌信息*/
    @Override
    public void updateBrand(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    /* 根据id获取品牌*/
    @Override
    public TbBrand getBrandById(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteById(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample tbBrandExample = new TbBrandExample();
        Criteria criteria = tbBrandExample.createCriteria();
        if(brand != null){
            if(brand.getName() != null && brand.getName().length() >0){
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if(brand.getFirstChar() != null && brand.getFirstChar().length() >0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand>  page = (Page<TbBrand>) brandMapper.selectByExample(tbBrandExample);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}