package com.shopaholicParadise.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String email;
	private String password;
	private String cpassword;
	private String profileImage;
	private String role;
	private Boolean isEnabled;
	private Boolean accountNonLocked;
	private Integer failedAttempt;
	private Date lockTime;
	private String resetToken;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCpassword() {
		return cpassword;
	}
	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}
	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	public Integer getFailedAttempt() {
		return failedAttempt;
	}
	public void setFailedAttempt(Integer failedAttempt) {
		this.failedAttempt = failedAttempt;
	}
	public Date getLockTime() {
		return lockTime;
	}
	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	public User(Integer id, String name, String email, String password, String cpassword, String profileImage,
			String role, Boolean isEnabled, Boolean accountNonLocked, Integer failedAttempt, Date lockTime,
			String resetToken) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.cpassword = cpassword;
		this.profileImage = profileImage;
		this.role = role;
		this.isEnabled = isEnabled;
		this.accountNonLocked = accountNonLocked;
		this.failedAttempt = failedAttempt;
		this.lockTime = lockTime;
		this.resetToken = resetToken;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
}