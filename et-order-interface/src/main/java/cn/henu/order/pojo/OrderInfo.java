package cn.henu.order.pojo;

import java.io.Serializable;
import java.util.List;

import cn.henu.pojo.TbOrder;
import cn.henu.pojo.TbOrderItem;
import cn.henu.pojo.TbOrderShipping;
/**
 * 用于接收提交订单的数据
 * @author syw
 *
 */
public class OrderInfo extends TbOrder implements Serializable{

	private List<TbOrderItem> orderItems;
	private TbOrderShipping orderShipping;
	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	
}
