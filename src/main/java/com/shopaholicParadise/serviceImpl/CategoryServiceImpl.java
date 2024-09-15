package com.shopaholicParadise.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.shopaholicParadise.model.Category;
import com.shopaholicParadise.repository.CategoryRepository;
import com.shopaholicParadise.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	@Override
	public Boolean isCategoryExists(String categoryName) {
		return categoryRepository.existsBycategoryName(categoryName);
	}

	@Override
	public Boolean deleteCategory(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(category)) {
			categoryRepository.delete(category);
			return true;
		}
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		return category;
	}
	/*
	 * @Override public List<Category> getAllActiveCategories() { List<Category>
	 * categories = categoryRepository.findByisActiveTrue();
	 * System.out.println(categories); return categories; }
	 */

	@Override
	public long getTotalCategories() {
		return categoryRepository.count();
	}

	@Override
	public Page<Category> getAllCategoriesPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo,pageSize);
		return categoryRepository.findAll(pageable);
		
	}

}
