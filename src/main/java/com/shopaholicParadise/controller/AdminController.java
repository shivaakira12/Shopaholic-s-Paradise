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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.Category;
import com.shopaholicParadise.model.Message;
import com.shopaholicParadise.model.Product;
import com.shopaholicParadise.model.ProductOrder;
import com.shopaholicParadise.model.User;
import com.shopaholicParadise.repository.CartRepository;
import com.shopaholicParadise.repository.MessageRepository;
import com.shopaholicParadise.service.CartService;
import com.shopaholicParadise.service.CategoryService;
import com.shopaholicParadise.service.MessageService;
import com.shopaholicParadise.service.OrderService;
import com.shopaholicParadise.service.ProductService;
import com.shopaholicParadise.service.UserService;
import com.shopaholicParadise.util.CommonUtil;
import com.shopaholicParadise.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

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
	public String index(Model model) {
		long totalMessages = messageService.getTotalMessages();
		List<Message> messages = messageService.getAllMessages();
		long totalUsers = userService.getTotalUsers();
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalMessages", totalMessages);
		model.addAttribute("messages", messages);
		return "admin/index";
	}

	@GetMapping("/supportmessages")
	public String loadsupportmessages(Model model) {
		List<Message> messages = messageService.getAllMessages();
		model.addAttribute("messages", messages);
		return "admin/supportmessages";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProductPage(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "/admin/addProduct";
	}

	@GetMapping("/loadAddCategory")
	public String loadAddCategoryPage(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize) {
		Page<Category> page = categoryService.getAllCategoriesPagination(pageNo, pageSize);
		m.addAttribute("categories", page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		return "/admin/addCategory";
	}

	// Add Category Logic implementation
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, HttpSession session,
			@RequestParam("file") MultipartFile file) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);
		if (categoryService.isCategoryExists(category.getCategoryName())) {
			session.setAttribute("errorMsg", "Category Already Exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Internal Server Error !");
			} else {
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_images" + File.separator
						+ file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("successMsg", "Category Saved Successfully");
			}

		}
		return "redirect:/admin/loadAddCategory";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("deleteMsg", "Category Deleted Successfully");
		} else {
			session.setAttribute("errorMsg", "Internal Server Error !");
		}
		return "redirect:/admin/loadAddCategory";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCatgegory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/editCategory";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category updateCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? updateCategory.getImageName() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_images" + File.separator
						+ file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			updateCategory.setCategoryName(category.getCategoryName());
			updateCategory.setIsActive(category.getIsActive());
			updateCategory.setImageName(imageName);
		}
		Category saveUpatedCategory = categoryService.saveCategory(updateCategory);
		if (!ObjectUtils.isEmpty(saveUpatedCategory)) {
			session.setAttribute("successMsg", "Updated Category Successfully !");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}
		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getProductprice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/images").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_images" + File.separator
					+ image.getOriginalFilename());

			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("successMsg", "Product Saved Success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/loadViewProduts")
	public String loadViewProducts(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize) {
		// m.addAttribute("allProducts", productService.getAllProducts());
		Page<Product> page = productService.getProductsWithPaginationInAdmin(pageNo, pageSize);
		m.addAttribute("allProducts", page);
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		return "admin/Products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("deleteMsg", "Product Deleted Successfully");
		} else {
			session.setAttribute("errorMsg", "Internal Server Issue");
		}
		return "redirect:/admin/loadViewProduts";
	}

	@GetMapping("/editProduct/{id}")
	public String editProducts(@PathVariable int id, Model m, HttpSession session) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/editProduct";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, HttpSession session, Model m,
			@RequestParam("file") MultipartFile image) {
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Discount Should between 0 & 100 only.");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("successMsg", "Product updated Successfully");
			} else {
				session.setAttribute("errorMsg", "Internal Server Issue");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

	@GetMapping("/supportMessage/{id}")
	public String supportMessageDelete(@PathVariable int id, HttpSession session) {
		Boolean deleteSupportMessage = messageService.deleteMessage(id);
		if (deleteSupportMessage) {
			session.setAttribute("deleteMsg", "Message Deleted Successfully");
		} else {
			session.setAttribute("errorMsg", "Internal Server Issue");
		}
		return "redirect:/admin/supportmessages";
	}

	@GetMapping("/loadUsers")
	public String loadUsers(Model model) {
		// List<User> users = userService.getAllUsers();
		List<User> listofUsers = userService.getUsers("ROLE_USER");
		model.addAttribute("listofUsers", listofUsers);
		// model.addAttribute("users", users);
		return "admin/loadUsers";
	}

	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable Integer id, HttpSession session) {
		Boolean deleteUser = userService.deleteUser(id);
		if (deleteUser) {
			session.setAttribute("deleteMsg", "Message Deleted Successfully");
		} else {
			session.setAttribute("errorMsg", "Internal Server Issue");
		}
		return "redirect:/admin/loadUsers";
	}

	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam Boolean isEnabled, @RequestParam Integer id,
			HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, isEnabled);
		if (f) {
			session.setAttribute("successMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "internal server issue");
		}
		return "redirect:/admin/loadUsers";
	}

	@GetMapping("/loadAdminOrders")
	public String loadAdminOrdersPage(Model model, HttpSession session,
			@RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize) {

		Page<ProductOrder> page = orderService.getAllOrderwithPagination(pageNo, pageSize);
		model.addAttribute("allOrder", page.getContent());
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());

		return "/admin/loadAdminOrders";
	}

	@PostMapping("/updateOrderStatus")
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
		return "redirect:/admin/loadAdminOrders";
	}
}
