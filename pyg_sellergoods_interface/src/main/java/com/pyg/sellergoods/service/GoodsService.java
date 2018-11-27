package com.pyg.sellergoods.service;
import java.util.List;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbItem;
import com.pyg.pojogroup.Goods;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	public List<TbGoods> findAll();


	/**
	 * 返回分页列表
	 *
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	 */
	//public void add(TbGoods goods);
	public void add(Goods goods);


	/**
	 * 修改
	 */
	public void update(TbGoods goods);

	public void update(Goods goods);

	/**
	 * 根据ID获取实体
	 *
	 * @param id
	 * @return
	 */
	/*	public TbGoods findOne(Long id);*/
	public Goods findOne(Long id);

	/**
	 * 批量删除
	 *
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 *
	 * @param pageNum  当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	public void updateStatus(Long[] ids, String status);

	//根据id和status获取商品列表
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status);
}
