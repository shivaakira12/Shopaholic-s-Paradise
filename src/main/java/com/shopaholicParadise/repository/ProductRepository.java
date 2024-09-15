package com.shopaholicParadise.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopaholicParadise.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	public List<Product> findByCategory(String category);

	List<Product> findByIsActiveTrue();

	@Query(value = "SELECT * FROM product ORDER BY id DESC LIMIT :count", nativeQuery = true)
	List<Product> findTopNProducts(@Param("count") int count);

	public Page<Product> findByIsActiveTrue(Pageable pageable);

	public Page<Product> findByCategory(Pageable pageable,String category);
}
