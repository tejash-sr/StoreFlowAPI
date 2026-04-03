package com.grootan.storeflow.service;

import java.util.UUID;

public interface NotificationService {
    void notifyOrderStatusChange(UUID orderId, String newStatus);
}
