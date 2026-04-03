package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendWelcomeEmail(String to, String fullName) {
        sendHtmlEmail(to, "Welcome to StoreFlow!",
                "<h1>Welcome, " + fullName + "!</h1><p>Your account is ready. Start browsing products.</p>");
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String link = "http://localhost:8080/api/auth/reset-password/" + resetToken;
        sendHtmlEmail(to, "Reset Your Password",
                "<h1>Password Reset Request</h1><p>Click <a href=\"" + link + "\">here</a> to reset your password. This link expires in 1 hour.</p>");
    }

    @Override
    public void sendOrderConfirmationEmail(String to, String referenceNumber, String totalAmount) {
        sendHtmlEmail(to, "Order Confirmation - " + referenceNumber,
                "<h1>Order Confirmed</h1><p>Order: " + referenceNumber + "</p><p>Total: $" + totalAmount + "</p><p>Thank you for your purchase.</p>");
    }

    @Override
    public void sendLowStockAlert(String to, String productName, int threshold) {
        sendHtmlEmail(to, "Low Stock Alert: " + productName,
                "<h1>Low Stock Warning</h1><p>Product \"" + productName + "\" has fallen below the threshold of " + threshold + " units.</p><p>Please restock soon.</p>");
    }

    @Override
    public void sendDailyDigest(String to, int orderCount) {
        sendHtmlEmail(to, "Daily Order Digest",
                "<h1>Daily Digest</h1><p>You had " + orderCount + " order(s) today.</p><p>Login to your dashboard for details.</p>");
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
