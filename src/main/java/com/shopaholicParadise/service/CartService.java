package com.shopaholicParadise.service;

import java.util.List;

import com.shopaholicParadise.model.Cart;

public interface CartService {

	public Cart saveCart(Integer productId, Integer userId);

	public List<Cart> getCartByUser(Integer userId);
	
	public Integer getCountCart(Integer userId);

	public void updateQuantity(String sy, Integer cid);
}
