package com.pyg.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.Result;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/9  0:35
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    /* 显示所有品牌列表*/
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /* 分页显示所有品牌列表*/
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int rows){
        return brandService.findPage(page,rows);
    }

    /*添加品牌*/
    @RequestMapping("/addBrand")
    public Result addBrand(@RequestBody TbBrand tbBrand){
        try{
            brandService.addBrand(tbBrand);
            return new Result(true,"增加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"增加失败");
        }
    }

    /*  根据Id获取品牌信息*/
    @RequestMapping("/findById")
    public TbBrand getBrandById(Long id){
        return brandService.getBrandById(id);
    }

    /* 修改指定的品牌*/
    @RequestMapping("/updateBrand")
    public Result updateBrand(@RequestBody TbBrand tbBrand){
        try{
            brandService.updateBrand(tbBrand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }


    @RequestMapping("/deleteByIds")
    public Result deleteByids(Long[] ids){
        try{
            brandService.deleteById(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand,int page,int rows){
        return brandService.findPage(tbBrand,page,rows);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}

