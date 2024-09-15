package com.shopaholicParadise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopaholicParadise.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	public Boolean existsBycategoryName(String categoryName);
	
	long count();
}
