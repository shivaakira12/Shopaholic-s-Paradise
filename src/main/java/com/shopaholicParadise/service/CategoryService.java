package com.shopaholicParadise.service;

import java.util.List;

import org.springframework.data.domain.Page;


import com.shopaholicParadise.model.Category;

public interface CategoryService {
	public Category saveCategory(Category category);
	public Boolean isCategoryExists(String categoryName);
	public List<Category> getAllCategory();
	public Boolean deleteCategory(int id);
	public Category getCategoryById(int id);
	public long getTotalCategories();
	//public List<Category> getAllActiveCategories();
	public Page <Category> getAllCategoriesPagination(Integer pageNo,Integer pageSize);
}
