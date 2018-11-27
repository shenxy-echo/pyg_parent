package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.entity.PageResult;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.pojo.TbSeckillOrderExample;
import com.pyg.pojo.TbSeckillOrderExample.Criteria;
import com.pyg.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private IdWorker idWorker;

	public void submitOrder(Long goodsId, String userId) {
		TbSeckillGoods seckillGoods =
				(TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(goodsId);
		if(seckillGoods ==null){
			throw new RuntimeException("商品不存在");
		}
		if(seckillGoods.getStockCount() <= 0){//商品全部抢光
			throw new RuntimeException("商品已抢购一空");
		}
		seckillGoods.setStockCount(seckillGoods.getStockCount() -1);//库存减一
		redisTemplate.boundHashOps("seckillGoods").put(goodsId,seckillGoods);//库存减一后更新缓存

		//当库存为0时，更新到数据库中，并删除缓存中的数据
		if(seckillGoods.getStockCount() == 0){
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps("seckillGoods").delete(goodsId);
		}

		//保存订单
		long orderId = idWorker.nextId();
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		seckillOrder.setId(orderId);
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setMoney(seckillGoods.getCostPrice());
		seckillOrder.setSeckillId(goodsId);
		seckillOrder.setUserId(userId);
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setStatus("0");
		redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
	}

	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		return seckillOrder;
	}

	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if(seckillOrder == null){
			throw new RuntimeException("订单不存在");
		}
		seckillOrder.setTransactionId(transactionId);
		seckillOrder.setStatus("1");
		seckillOrder.setPayTime(new Date());
		seckillOrderMapper.insert(seckillOrder);
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}


	/**
	 * 支付超时的业务处理
	 *
	 * @param userid
	 * @param
	 */
	@Override
	public void deleteOrderFromRedis(String userid, Long seckillOrderId) {
		//1.删除redis中秒杀订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userid);

		if(seckillOrder != null){
			redisTemplate.boundHashOps("seckillOrder").delete(userid);
		}
		//2. 将库存恢复
		//根据秒杀商品id,读取redis中的秒杀商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());

		//恢复库存
		seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
		redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
	}
}
