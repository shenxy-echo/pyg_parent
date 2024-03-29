package com.pyg.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbItemCat;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbItemCat> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbItemCat itemCat);
	
	
	/**
	 * 修改
	 */
	public void update(TbItemCat itemCat);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbItemCat findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);
	public void deleteAll(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(long id, int pageNum, int pageSize);

	/**
	* @author 沈小云
	* @description  根据父ID查询分类信息
	* @date 2018/10/13 10:13
	* @param
	*@return com.pyg.pojo.TbItemCat
	*/
	public List<TbItemCat> getItemCatByPid(Long id);

	List<Map> selectCatList(Long id);
}
