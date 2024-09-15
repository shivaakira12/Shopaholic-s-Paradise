package com.shopaholicParadise.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.Product;

public interface ProductService {
	public Product saveProduct(Product product);

	public List<Product> getAllProducts();

	public Boolean deleteProduct(Integer id);

	public Product getProductById(Integer id);

	public Product updateProduct(Product product, MultipartFile image);

	public List<Product> getAllIsActiveProducts(String category);

	List<Product> getLatestProducts(int count);
	
	Page<Product> getLatestProductswithPagination(Integer pageNo,Integer pageSize);

	public Page<Product> getProductsWithPaginationInAdmin(Integer pageNo, Integer pageSize);
	
	Page<Product> getAllActiveProductByPagination(Integer pageNo, Integer pageSize, String category);
}
