package com.shopaholicParadise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length=500)
	private String producttitle;
	@Column(length=5000)
	private String productdescription;
	private String category;
	private Double productprice;
	private int productStock;
	private String image;
	private int discount;
	private Double discountPrice;
	private String isActive;
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Product(int id, String producttitle, String productdescription, String category, Double productprice,
			int productStock, String image, int discount, Double discountPrice, String isActive) {
		super();
		this.id = id;
		this.producttitle = producttitle;
		this.productdescription = productdescription;
		this.category = category;
		this.productprice = productprice;
		this.productStock = productStock;
		this.image = image;
		this.discount = discount;
		this.discountPrice = discountPrice;
		this.isActive = isActive;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProducttitle() {
		return producttitle;
	}
	public void setProducttitle(String producttitle) {
		this.producttitle = producttitle;
	}
	public String getProductdescription() {
		return productdescription;
	}
	public void setProductdescription(String productdescription) {
		this.productdescription = productdescription;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Double getProductprice() {
		return productprice;
	}
	public void setProductprice(Double productprice) {
		this.productprice = productprice;
	}
	public int getProductStock() {
		return productStock;
	}
	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public Double getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
}
	