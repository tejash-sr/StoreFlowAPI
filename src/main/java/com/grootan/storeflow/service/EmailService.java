package com.grootan.storeflow.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String fullName);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendOrderConfirmationEmail(String to, String referenceNumber, String totalAmount);
    void sendLowStockAlert(String to, String productName, int threshold);
    void sendDailyDigest(String to, int orderCount);
}
