package com.pyg.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pyg.entity.PageResult;
import com.pyg.mapper.*;
import com.pyg.pojo.*;
import com.pyg.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.pojo.TbGoodsExample.Criteria;
import com.pyg.sellergoods.service.GoodsService;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}



	/**
	 * 增加
	 */
	/*@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);		
	}*/
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");//设置未审核状态
		goodsMapper.insert(tbGoods);
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		tbGoodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(tbGoodsDesc);
		setItemValues(goods);

	}

    private void setItemValues(Goods goods){
        String isEnableSpec = goods.getTbGoods().getIsEnableSpec();
        if("1".equals(isEnableSpec)){
            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getTbGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(tbItem.getSpec());
                for (String s : specMap.keySet()) {
                    title += " " + specMap.get(s);
                }
                tbItem.setTitle(title);
                tbItem.setGoodsId(goods.getTbGoods().getId());//商品SPU编号
                tbItem.setSellerId(goods.getTbGoods().getSellerId());//商家编号
                tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类编号（3级）
                tbItem.setCreateTime(new Date());//创建日期
                tbItem.setUpdateTime(new Date());//修改日期

                //品牌名称
                TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
                tbItem.setBrand(brand.getName());
                //分类名称
                TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
                tbItem.setCategory(itemCat.getName());

                //商家名称
                TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
                tbItem.setSeller(seller.getNickName());

                //图片地址（取spu的第一个图片）
                List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class) ;
                if(imageList.size()>0){
                    tbItem.setImage ( (String)imageList.get(0).get("url"));
                }
                itemMapper.insert(tbItem);
            }
        }else{
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getTbGoods().getGoodsName());
            tbItem.setPrice(goods.getTbGoods().getPrice());
            tbItem.setNum(99999);
            tbItem.setSpec("{}");
            tbItem.setStatus("1");//状态
            tbItem.setIsDefault("1");//是否默认
            tbItem.setGoodsId(goods.getTbGoods().getId());//商品SPU编号
            tbItem.setSellerId(goods.getTbGoods().getSellerId());//商家编号
            tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类编号（3级）
            tbItem.setCreateTime(new Date());//创建日期
            tbItem.setUpdateTime(new Date());//修改日期

            //品牌名称
            TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
            tbItem.setBrand(brand.getName());
            //分类名称
            TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
            tbItem.setCategory(itemCat.getName());

            //商家名称
            TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
            tbItem.setSeller(seller.getNickName());

            //图片地址（取spu的第一个图片）
            List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class) ;
            if(imageList.size()>0){
                tbItem.setImage ( (String)imageList.get(0).get("url"));
            }
            itemMapper.insert(tbItem);
        }
    }

	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}

    @Override
    public void update(Goods goods) {
        goods.getTbGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
        itemMapper.deleteByExample(example);
        setItemValues(goods);
    }

    /**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	/*public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}*/
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setTbGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setTbGoodsDesc(tbGoodsDesc);
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItemList = itemMapper.selectByExample(tbItemExample);
		goods.setItemList(tbItemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			criteria.andIsDeleteIsNull();
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            String auditStatus = tbGoods.getAuditStatus();
            System.out.println("**************"+auditStatus);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo(status);
		List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
		return tbItems;
	}



}
