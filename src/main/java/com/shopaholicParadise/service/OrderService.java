package com.shopaholicParadise.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.shopaholicParadise.model.OrderRequest;
import com.shopaholicParadise.model.ProductOrder;

public interface OrderService {

	public void saveOrder(Integer userid, OrderRequest orderRequest);

	public List<ProductOrder> getOrdersByUser(Integer userId);
	
	public Page<ProductOrder> getOrdersByUserPagination(Integer pageNo,Integer pageSize);

	public ProductOrder updateOrderStatus(Integer id, String status);
	
	public List<ProductOrder> getAllOrder();
	
	public Page<ProductOrder> getAllOrderwithPagination(Integer pageNo,Integer pageSize);

}