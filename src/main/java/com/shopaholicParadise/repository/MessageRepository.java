package com.shopaholicParadise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopaholicParadise.model.Message;
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
	 long count(); 
}
  