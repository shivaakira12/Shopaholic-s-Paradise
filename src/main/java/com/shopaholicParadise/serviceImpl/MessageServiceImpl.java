package com.shopaholicParadise.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopaholicParadise.model.Message;
import com.shopaholicParadise.repository.MessageRepository;
import com.shopaholicParadise.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Override
	public long getTotalMessages() {
		return messageRepository.count();
	}

	@Override
	public List<Message> getAllMessages() {
		return messageRepository.findAll();
	}

	@Override
	public Boolean deleteMessage(Integer id) {
		try {
			messageRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
