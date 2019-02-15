package cn.henu.order.service;

import cn.henu.common.utils.EtResult;
import cn.henu.order.pojo.OrderInfo;

public interface OrderService {

	EtResult createOrder(OrderInfo orderInfo);
}
