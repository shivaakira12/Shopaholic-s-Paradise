package com.shopaholicParadise.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.shopaholicParadise.model.Cart;
import com.shopaholicParadise.model.Product;
import com.shopaholicParadise.model.User;
import com.shopaholicParadise.repository.CartRepository;
import com.shopaholicParadise.repository.ProductRepository;
import com.shopaholicParadise.repository.UserRepository;
import com.shopaholicParadise.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {

		User userDetails = userRepository.findById(userId).get();
		Product productDetails = productRepository.findById(productId).get();
		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart = null;
		if (ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(productDetails);
			cart.setUser(userDetails);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * productDetails.getDiscountPrice());
		} else {
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}
		Cart saveCart = cartRepository.save(cart);
		return saveCart;
	}

	@Override
	public List<Cart> getCartByUser(Integer userId) {
		List<Cart> totalCart = cartRepository.findByUserId(userId);
		BigDecimal totalOrderPrice = BigDecimal.ZERO;
		List<Cart> updatedCart = new ArrayList<>();

		for (Cart c : totalCart) {
			BigDecimal discountPrice = BigDecimal.valueOf(c.getProduct().getDiscountPrice());
			BigDecimal quantity = BigDecimal.valueOf(c.getQuantity());
			BigDecimal totalPrice = discountPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);

			c.setTotalPrice(totalPrice.doubleValue());
			totalOrderPrice = totalOrderPrice.add(totalPrice).setScale(2, RoundingMode.HALF_UP);

			c.setTotalOrderPrice(totalOrderPrice.doubleValue());
			updatedCart.add(c);
		}

		return updatedCart;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepository.countByUserId(userId);
		System.out.println(countByUserId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		Cart cart = cartRepository.findById(cid).get();

		int updateQuantity;

		if (sy.equals("de")) {
			updateQuantity = cart.getQuantity() - 1;
			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}
		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}

	}
}
