package com.shopaholicParadise.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.shopaholicParadise.model.User;
import com.shopaholicParadise.repository.UserRepository;
import com.shopaholicParadise.service.UserService;
import com.shopaholicParadise.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String email = request.getParameter("username");

		User userDtls = userRepository.findByEmail(email);
		if (userDtls != null) {
			if (userDtls.getIsEnabled()) {
				if (userDtls.getAccountNonLocked()) {
					if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
						userService.increaseFailedAttempt(userDtls);
					} else {
						userService.userAccountLock(userDtls);
						exception = new LockedException(
								"Your account has been locked due to multiple failed login attempts.");
					}
				} else {
					if (userService.unlockAccountTimeExpired(userDtls)) {
						exception = new LockedException("Your account has been unlocked. Please try to log in again.");
					} else {
						exception = new LockedException("Your account is locked. Please try again later.");
					}
				}
			} else {
				exception = new LockedException("Your account is inactive.");
			}
		} else {
			exception = new LockedException("Invalid email address.");
		}
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
