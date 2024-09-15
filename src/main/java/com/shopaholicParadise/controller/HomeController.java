
package com.shopaholicParadise.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.Category;
import com.shopaholicParadise.model.Message;
import com.shopaholicParadise.model.Product;
import com.shopaholicParadise.model.ProductOrder;
import com.shopaholicParadise.model.User;
import com.shopaholicParadise.repository.MessageRepository;
import com.shopaholicParadise.service.CartService;
import com.shopaholicParadise.service.CategoryService;
import com.shopaholicParadise.service.ProductService;
import com.shopaholicParadise.service.UserService;
import com.shopaholicParadise.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private CartService cartService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User userDetails = userService.getUserByEmail(email);
			m.addAttribute("user", userDetails);
			Integer cartCount = cartService.getCountCart(userDetails.getId());
			m.addAttribute("cartCount", cartCount);
		}
	}

	@GetMapping("/")
	public String index(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
		// List<Product> latestProducts = productService.getLatestProducts(10);
		// m.addAttribute("latestProducts", latestProducts);
		Page<Product> page = productService.getLatestProductswithPagination(pageNo, pageSize);
		model.addAttribute("latestProducts", page.getContent());
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		Boolean userExists = userService.userExistsByEmail(user.getEmail());
		if (userExists) {
			session.setAttribute("errorMsg", "User Already Exists");
		} else {

			String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);

			User saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/images").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_images"
							+ File.separator + file.getOriginalFilename());

					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				session.setAttribute("successMsg", "Register successfully");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}

		}

		return "redirect:/register";
	}

	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		Page<Product> page = productService.getAllActiveProductByPagination(pageNo, pageSize, category);
		List<Product> products = page.getContent();
		m.addAttribute("products", products);
		m.addAttribute("productSize", products.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		return "products";
	}

	@GetMapping("/product/{id}")
	public String viewProductsPage(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "viewProduct";
	}

	@GetMapping("/about")
	public String aboutPage() {
		return "about";
	}

	@GetMapping("/contact")
	public String contactPage() {
		return "contact";
	}

	@Autowired
	private MessageRepository messageRepository;

	@PostMapping("/sendMessage")
	public String sendMessage(@RequestParam String name, @RequestParam String email, @RequestParam String number,
			@RequestParam String msg) {
		Message message = new Message();
		message.setName(name);
		message.setEmail(email);
		message.setNumber(number);
		message.setMsg(msg);
		messageRepository.save(message);
		return "contact";
	}

	/*
	 * Forgot password implementation
	 */
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String showProcessForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		User userDetails = userService.getUserByEmail(email);
		if (ObjectUtils.isEmpty(userDetails)) {
			session.setAttribute("errorMsg", "Email not found");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

			String url = CommonUtil.generateUrl(request) + "/reset_password?token=" + resetToken;
			Boolean sendMail = commonUtil.sendMail(url, email);
			if (sendMail) {
				session.setAttribute("successMsg", "Verification link sent to your Email");
			} else {
				session.setAttribute("errorMsg", "Something went wrong on Server");
			}

		}
		return "redirect:/forgot-password";
	}

	@GetMapping("/reset_password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		User userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			m.addAttribute("msg", "Your link is invalid or expired !!");
			return "errorPage";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset_password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {

		User userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("msg", "Your link is invalid or expired !!");
			return "errorPage";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
//			session.setAttribute("msg", "Password change successfully");
			m.addAttribute("msg", "Password change successfully");
			return "errorPage";
		}

	}

}
