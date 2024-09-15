package com.shopaholicParadise.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.Cart;
import com.shopaholicParadise.model.OrderRequest;
import com.shopaholicParadise.model.ProductOrder;
import com.shopaholicParadise.model.User;
import com.shopaholicParadise.service.CartService;
import com.shopaholicParadise.service.OrderService;
import com.shopaholicParadise.service.UserService;
import com.shopaholicParadise.util.CommonUtil;
import com.shopaholicParadise.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@GetMapping("/")
	public String home(Model model) {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User userDetails = userService.getUserByEmail(email);
			if (userDetails != null) {
				m.addAttribute("user", userDetails);
				Integer cartCount = cartService.getCountCart(userDetails.getId());
				m.addAttribute("cartCount", cartCount);
				System.out.println("User Details: " + userDetails);
			} else {
				System.out.println("No user found for email: " + email);
			}
		} else {
			System.out.println("Principal is null");
		}
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("successMsg", "Product added to Cart");
		}
		return "redirect:/product/" + pid;
	}

	@GetMapping("/cartPage")
	public String loadCartPage(Principal p, Model m) {

		User userDetails = getLoggedInUserDetails(p);
		List<Cart> cartList = cartService.getCartByUser(userDetails.getId());
		m.addAttribute("carts", cartList);
		if (cartList.size() > 0) {
			Double totalCartOrderedPrice = cartList.get(cartList.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalCartOrderedPrice);
		}
		return "/user/cart";
	}

	private User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User userEmail = userService.getUserByEmail(email);
		return userEmail;
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cartPage";
	}

	@GetMapping("/orders")
	public String loadOrderPage(Principal p, Model m) {
		User userDetails = getLoggedInUserDetails(p);
		List<Cart> cartList = cartService.getCartByUser(userDetails.getId());
		m.addAttribute("carts", cartList);
		if (cartList.size() > 0) {
			Double OrderedPrice = cartList.get(cartList.size() - 1).getTotalOrderPrice();
			Double priceWithTax = OrderedPrice + 10;
			System.out.println("OrderedPrice" + OrderedPrice);
			m.addAttribute("totalOrderPrice", String.format("%.2f", OrderedPrice));
			m.addAttribute("priceWithTax", String.format("%.2f", priceWithTax));
		}
		return "/user/order";
	}

	@PostMapping("/saveOrder")
	public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal p) {

		User userLogged = getLoggedInUserDetails(p);
		orderService.saveOrder(userLogged.getId(), orderRequest);
		return "/user/orderSuccess";
	}

	@GetMapping("/userOrders")
	public String loadUserOrders(Principal p, Model m,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize) {
		User userDetails = getLoggedInUserDetails(p);
		Page<ProductOrder> page = orderService.getAllOrderwithPagination(pageNo, pageSize);
		m.addAttribute("ordersByUser",page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		return "/user/userOrders";
	}

	@GetMapping("/updateOrderStatus")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
		OrderStatus[] values = OrderStatus.values();
		String status = null;
		for (OrderStatus orderStatus : values) {
			if (orderStatus.getId().equals(st)) {
				status = orderStatus.getName();
			}
		}
		ProductOrder updateOrderStatus = orderService.updateOrderStatus(id, status);
		try {
			commonUtil.sendMailStatusForUserOrder(updateOrderStatus, status);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		if (!ObjectUtils.isEmpty(updateOrderStatus)) {
			session.setAttribute("successMsg", "Order Cancelled");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on Server");
		}
		return "redirect:/user/userOrders";
	}

	@GetMapping("/userProfile")
	public String userProfile() {
		return "/user/userProfile";
	}
	/*
	 * @PostMapping("/updateProfile") public String updateProfile(@ModelAttribute
	 * User user,@RequestParam MultipartFile img,HttpSession session) { User
	 * updatedDetails = userService.updateUserDetails(user, img);
	 * if(ObjectUtils.isEmpty(updatedDetails)) { session.setAttribute("successMsg",
	 * "Order Cancelled"); } else { session.setAttribute("errorMsg",
	 * "Something went wrong on Server"); } return "redirect:/user/userProfile"; }
	 */

}
