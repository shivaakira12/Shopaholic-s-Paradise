package com.shopaholicParadise.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopaholicParadise.model.Cart;
import com.shopaholicParadise.model.OrderDetails;
import com.shopaholicParadise.model.OrderRequest;
import com.shopaholicParadise.model.ProductOrder;
import com.shopaholicParadise.repository.CartRepository;
import com.shopaholicParadise.repository.ProductOrderRepository;
import com.shopaholicParadise.service.OrderService;
import com.shopaholicParadise.util.CommonUtil;
import com.shopaholicParadise.util.OrderStatus;

import jakarta.mail.MessagingException;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository productOrderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) {
		List<Cart> carts = cartRepository.findByUserId(userid);
		for (Cart cart : carts) {

			ProductOrder productOrder = new ProductOrder();
			productOrder.setOrderId(UUID.randomUUID().toString());
			productOrder.setOrderDate(LocalDate.now());
			productOrder.setProduct(cart.getProduct());
			productOrder.setPrice(cart.getProduct().getDiscountPrice());
			productOrder.setQuantity(cart.getQuantity());
			productOrder.setUser(cart.getUser());
			productOrder.setStatus(OrderStatus.IN_PROGRESS.name());
			productOrder.setPaymentType(orderRequest.getPaymentType());

			OrderDetails orderDetails = new OrderDetails();
			orderDetails.setFirstName(orderRequest.getFirstName());
			orderDetails.setLastName(orderRequest.getLastName());
			orderDetails.setEmail(orderRequest.getEmail());
			orderDetails.setMobileNumber(orderRequest.getMobileNumber());
			orderDetails.setAddress(orderRequest.getAddress());
			orderDetails.setCity(orderRequest.getCity());
			orderDetails.setState(orderRequest.getState());
			orderDetails.setPincode(orderRequest.getPincode());
			productOrder.setOrderDetails(orderDetails);
			ProductOrder save = productOrderRepository.save(productOrder);
			try {
				commonUtil.sendMailStatusForUserOrder(productOrder, "success");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> userOrders = productOrderRepository.findByUserId(userId);
		return userOrders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = productOrderRepository.findById(id);
		if (findById.isPresent()) {
			ProductOrder p = findById.get();
			p.setStatus(status);
			ProductOrder save = productOrderRepository.save(p);
			return save;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrder() {
		List<ProductOrder> findAll = productOrderRepository.findAll();
		return findAll;
	}

	@Override
	public Page<ProductOrder> getAllOrderwithPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productOrderRepository.findAll(pageable);
	}

	@Override
	public Page<ProductOrder> getOrdersByUserPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productOrderRepository.findAll(pageable);
	}

}
