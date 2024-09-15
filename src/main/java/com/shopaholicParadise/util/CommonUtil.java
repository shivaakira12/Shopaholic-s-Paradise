package com.shopaholicParadise.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.shopaholicParadise.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("shivaakira000@gmail.com", "Groco");
		helper.setTo(reciepentEmail);

		String content = "<html>" + "<head>" + "<style>" + "p {" + "  font-family: Arial, sans-serif;"
				+ "  font-size: 14px;" + "  color: #333333;" + "}" + "a {" + "  color: #1a73e8;"
				+ "  text-decoration: none;" + "}" + "a:hover {" + "  text-decoration: underline;" + "}" + "body {"
				+ "  background-color: #f4f4f4;" + "  padding: 20px;" + "}" + "div {" + "  background-color: #ffffff;"
				+ "  padding: 20px;" + "  border: 1px solid #dddddd;" + "  border-radius: 5px;" + "}" + "</style>"
				+ "</head>" + "<body>" + "<div>" + "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>" + "</div>" + "</body>" + "</html>";

		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");

	}

	public Boolean sendMailStatusForUserOrder(ProductOrder productOrder, String status)
			throws MessagingException, UnsupportedEncodingException {
		String emailMessage;
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setFrom("shivaakira000@gmail.com", "Groco");
		helper.setTo(productOrder.getOrderDetails().getEmail());

		emailMessage = "<html>" + "<head>" + "<style>"
				+ "body { font-family: Arial, sans-serif; color: #333; line-height: 1.6; background-color: #f4f4f4; margin: 0; padding: 0; }"
				+ ".container { max-width: 600px; margin: 20px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }"
				+ "h1 { color: #1abc9c; }" + "h2 { color: #2c3e50; margin-bottom: 10px; }" + "p { margin: 0 0 15px; }"
				+ "strong { color: #333; }"
				+ ".footer { margin-top: 20px; font-size: 0.9em; color: #777; border-top: 1px solid #eee; padding-top: 10px; }"
				+ ".highlight { color: #e74c3c; }" + "</style>" + "</head>" + "<body>" + "<div class='container'>"
				+ "<h1>Order Status Update</h1>" + "<p>Dear " + productOrder.getOrderDetails().getEmail() + ",</p>"
				+ "<p>Thank you for choosing Groco for your recent purchase. We are excited to update you about the status of your order.</p>"
				+ "<p>We are pleased to inform you that your order has been <span class='highlight'>"
				+ productOrder.getStatus() + "</span>.</p>" + "<h2>Order Summary:</h2>"
				+ "<p><strong>Product Name:</strong> " + productOrder.getProduct().getProducttitle() + "</p>"
				+ "<p><strong>Category:</strong> " + productOrder.getProduct().getCategory() + "</p>"
				+ "<p><strong>Quantity:</strong> " + productOrder.getQuantity() + "</p>"
				+ "<p><strong>Price:</strong> " + productOrder.getPrice() + "</p>"
				+ "<p><strong>Payment Mode:</strong> " + productOrder.getPaymentType() + "</p>"
				+ "<p>If you have any questions or need further assistance, please do not hesitate to contact us.</p>"
				+ "<p>We appreciate your business and look forward to serving you again in the future!</p>"
				+ "<div class='footer'>" + "<p>Best regards,<br>Team Groco</p>"
				+ "<p><a href='' style='color: #1abc9c; text-decoration: none;'>Visit our website</a> | <a href='shivaakira000@gmail.com' style='color: #1abc9c; text-decoration: none;'>Contact Support</a></p>"
				+ "</div>" + "</div>" + "</body>" + "</html>";

		helper.setSubject("Your Groco Order Status");
		helper.setText(emailMessage, true);
		mailSender.send(message);
		return true;
	}

}
