package com.pyg.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbSpecificationMapper;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojo.TbSpecificationExample;
import com.pyg.sellergoods.service.SpecificationService;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加规格
	 */
	@Override
	public void add(TbSpecification specification) {
		specificationMapper.insert(specification);		
	}

	/**
	 * 增加规格和规格选项的包装类
	 */
	@Override
	public void add(Specification specification) {
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification);
        System.out.println("*****" + tbSpecification.getId());
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
			tbSpecificationOption.setSpecId(specification.getTbSpecification().getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}


	/**
	 * 修改
	 */
	@Override
/*	public void update(TbSpecification specification){
		specificationMapper.updateByPrimaryKey(specification);
	}	*/
    public void update(Specification specification){
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        TbSpecificationOptionExample tbSpecificationOptionExample
                = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(tbSpecificationOption);
        }
    }

    /**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	/*@Override
	public TbSpecification findOne(Long id){
		return specificationMapper.selectByPrimaryKey(id);
	}*/
    @Override
    public Specification findOne(Long id){
        Specification specification = new Specification();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOptionExample tbSpecificationOptionExample
                = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptionList =
                specificationOptionMapper.selectByExample(tbSpecificationOptionExample);
        specification.setTbSpecification(tbSpecification);
        specification.setSpecificationOptionList(tbSpecificationOptionList);
        return  specification;
    }
	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample tbSpecificationOptionExample
                    = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);
        }
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectSpeList() {
		return specificationMapper.selectSpeList();
	}

}
