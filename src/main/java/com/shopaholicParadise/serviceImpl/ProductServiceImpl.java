package com.shopaholicParadise.serviceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.Product;
import com.shopaholicParadise.repository.ProductRepository;
import com.shopaholicParadise.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {
		Product dbProduct = getProductById(product.getId());
		String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();
		dbProduct.setProducttitle(product.getProducttitle());
		// dbProduct.setImage(imageName);
		dbProduct.setCategory(product.getCategory());
		dbProduct.setProductprice(product.getProductprice());
		dbProduct.setProductStock(product.getProductStock());
		dbProduct.setProductdescription(product.getProductdescription());
		dbProduct.setDiscount(product.getDiscount());
		dbProduct.setIsActive(product.getIsActive());
		Double discount = product.getProductprice() * (product.getDiscount() / 100.0);
		Double discountPrice = product.getProductprice() - discount;
		dbProduct.setDiscountPrice(discountPrice);

		Product updateproduct = productRepository.save(dbProduct);
		if (!ObjectUtils.isEmpty(updateproduct)) {
			if (!image.isEmpty()) {
				try {
					File saveFile = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_images"
							+ File.separator + image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return product;
		}
		return null;
	}

	@Override
	public List<Product> getAllIsActiveProducts(String category) {
		List<Product> products = null;
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findAll();
		} else {
			products = productRepository.findByCategory(category);
		}
		return products;
	}

	@Override
	public List<Product> getLatestProducts(int count) {
		return productRepository.findTopNProducts(count);
	}

	@Override
	public Page<Product> getAllActiveProductByPagination(Integer pageNo, Integer pageSize, String category) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;
		if (ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findAll(pageable);
		} else {
			pageProduct = productRepository.findByCategory(pageable,category);
		}
		return pageProduct;
	}

	@Override
	public Page<Product> getLatestProductswithPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo,pageSize);
		return productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> getProductsWithPaginationInAdmin(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}

}
