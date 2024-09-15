package com.shopaholicParadise.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopaholicParadise.model.User;
import com.shopaholicParadise.repository.UserRepository;
import com.shopaholicParadise.service.UserService;
import com.shopaholicParadise.util.AppConstant;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User saveUser(User user) {
		user.setRole("ROLE_USER");
		user.setIsEnabled(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		// user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		User saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public long getTotalUsers() {
		return userRepository.count();
	}

	@Override
	public Boolean deleteUser(Integer id) {
		User user = userRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(user)) {
			userRepository.delete(user);
			return true;
		}
		return false;
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {

		Optional<User> findByUser = userRepository.findById(id);
		if (findByUser.isPresent()) {
			User userDetails = findByUser.get();
			userDetails.setIsEnabled(status);
			userRepository.save(userDetails);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(User user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
	}

	@Override
	public void userAccountLock(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
	}

	@Override
	public Boolean unlockAccountTimeExpired(User user) {

		long lockTime = user.getLockTime().getTime();
		long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		long currentTime = System.currentTimeMillis();
		if (unlockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void resetAttempt(int userId) {

	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		User findByEmail = userRepository.findByEmail(email);
		System.out.println(findByEmail.getEmail());
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
	}

	@Override
	public User getUserByToken(String token) {
		return userRepository.findByResetToken(token);
	}

	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public Boolean userExistsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	//@Override
//	public User updateUserDetails(User user, MultipartFile img) {
//		User updateUserDetails = userRepository.findById(user.getId()).get();
//		if (!img.isEmpty()) {
//			updateUserDetails.setProfileImage(img.getOriginalFilename());
//		}
//		User updatedDetails = null;
//		if (!ObjectUtils.isEmpty(updateUserDetails)) {
//			updateUserDetails.setName(user.getName());
//			updateUserDetails.setEmail(user.getEmail());
//			//updateUserDetails.setIsEnabled(user.getIsEnabled());
//			updatedDetails = userRepository.save(updateUserDetails);
//		}
//		if (!img.isEmpty()) {
//			File saveFile;
//			try {
//				saveFile = new ClassPathResource("static/images").getFile();
//				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_images" + File.separator
//						+ img.getOriginalFilename());
//
//				System.out.println(path);
//				Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//		return updatedDetails;
//	}

}
