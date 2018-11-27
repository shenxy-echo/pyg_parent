package com.pyg.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojogroup.Specification;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加规格
	*/
	public void add(TbSpecification specification);

	/**
	 * 增加规格和规格选项的包装类
	 */
	public void add(Specification specification);
	
	/**
	 * 修改
	 */
	//public void update(TbSpecification specification);
	public void update(Specification specification);
	

/*	public TbSpecification findOne(Long id);*/

	/**
	 * 根据ID获取规格和规格选项的包装类
	 * @param id
	 * @return
	 */
	public Specification findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize);

	List<Map> selectSpeList();


	
}
