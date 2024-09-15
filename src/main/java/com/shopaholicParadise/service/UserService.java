package com.shopaholicParadise.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.User;

public interface UserService {
	
	public User saveUser(User user);

	public User getUserByEmail(String email);

	public List<User> getUsers(String role);

	long getTotalUsers();

	Boolean deleteUser(Integer id);

	public Boolean updateAccountStatus(Integer id, Boolean status);
	
	public void increaseFailedAttempt(User user);
	
	public void userAccountLock(User user);
	
	public Boolean unlockAccountTimeExpired(User user);
	
	public void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);

	public User getUserByToken(String token);

	User updateUser(User user);
	
	public Boolean userExistsByEmail(String email);

//	User updateUserDetails(User user, MultipartFile img);
	
}
