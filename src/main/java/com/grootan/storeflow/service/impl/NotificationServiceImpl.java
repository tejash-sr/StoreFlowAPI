package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyOrderStatusChange(UUID orderId, String newStatus) {
        String destination = "/topic/orders/" + orderId + "/status";
        messagingTemplate.convertAndSend(destination, "Order status updated to: " + newStatus);
    }
}
