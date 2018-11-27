package com.pyg.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.pojo.TbItemCat;
import com.pyg.pojo.TbItemCatExample;
import com.pyg.pojo.TbItemCatExample.Criteria;
import com.pyg.sellergoods.service.ItemCatService;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}		
	}

	@Override
	public void deleteAll(Long[] ids) {
		for (Long id : ids) {
			TbItemCatExample example = new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(id);
			List<TbItemCat> tbItemCatList = itemCatMapper.selectByExample(example);
            itemCatMapper.deleteByPrimaryKey(id);
			if(tbItemCatList == null||tbItemCatList.size() == 0){
				continue;
			}else{
				List<Long> idList = new ArrayList<>();
				for (TbItemCat tbItemCat : tbItemCatList) {
					idList.add(tbItemCat.getId());
				}
				Long[] longs = new Long[idList.size()];
                Long[] id_s = idList.toArray(longs);
                deleteAll(id_s);
			}
		}
	}


	@Override
	public PageResult findPage(long id, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);

		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);
        List<TbItemCat> itemCatList = findAll();
        for (TbItemCat tbItemCat : itemCatList) {
            redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(),tbItemCat.getTypeId());
        }
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public List<TbItemCat> getItemCatByPid(Long id) {
		TbItemCatExample tbItemCatExample = new TbItemCatExample();
		Criteria criteria = tbItemCatExample.createCriteria();
		criteria.andParentIdEqualTo(id);
		List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(tbItemCatExample);
		return tbItemCats;
	}

    @Override
    public List<Map> selectCatList(Long id) {
        return itemCatMapper.selectCatList(id);
    }

}
