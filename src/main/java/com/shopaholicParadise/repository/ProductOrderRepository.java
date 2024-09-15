package com.shopaholicParadise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopaholicParadise.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer>{

	List<ProductOrder> findByUserId(Integer userId);

}
