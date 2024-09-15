package com.shopaholicParadise.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopaholicParadise.model.Message;
import com.shopaholicParadise.model.Product;
import com.shopaholicParadise.repository.MessageRepository;

public interface MessageService {

	long getTotalMessages();
	
	List<Message> getAllMessages();

	Boolean deleteMessage(Integer id);
}
