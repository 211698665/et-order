package cn.henu.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.henu.common.jedis.JedisClientCluster;
import cn.henu.common.utils.EtResult;
import cn.henu.mapper.TbOrderItemMapper;
import cn.henu.mapper.TbOrderMapper;
import cn.henu.mapper.TbOrderShippingMapper;
import cn.henu.order.pojo.OrderInfo;
import cn.henu.order.service.OrderService;
import cn.henu.pojo.TbOrderItem;
import cn.henu.pojo.TbOrderShipping;

/**
 * 订单处理服务
 * @author syw
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClientCluster jedisClientCluster;
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_START}")
	private String ORDER_ID_START;
	@Value("${ORDER_DETAIL_ID_GEN_KEY}")
	private String ORDER_DETAIL_ID_GEN_KEY;
	@Override
	public EtResult createOrder(OrderInfo orderInfo) {
		//生成订单号，使用redis的incr生成,如果是第一次
		if(!jedisClientCluster.exists(ORDER_ID_GEN_KEY)) {
			jedisClientCluster.set(ORDER_ID_GEN_KEY, ORDER_ID_START);
		}
		String orderId=jedisClientCluster.incr(ORDER_ID_GEN_KEY).toString();
		//补全orderinfo的属性
		orderInfo.setOrderId(orderId);
		//1.代表未付款，2代表已付款，3代表未发货，4,代表已发货，5代表交易成功，6代表交易关闭
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		//插入订单表
		orderMapper.insert(orderInfo);
		//向订单明细表插入数据
		List<TbOrderItem> list = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : list) {
			//生成明细id
			String odId=jedisClientCluster.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
			//补全pojo的属性
			tbOrderItem.setId(odId);
			tbOrderItem.setOrderId(orderId);
			//向明细表插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		//向订单物流表插入数据
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		
		//返回EtResult,其中包含订单号
		
		return EtResult.ok(orderId);
	}

}
