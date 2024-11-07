package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.Notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(List<Long> userId, Notification notification) {
        log.info("Sending WS notification to {} with payload {}", userId, notification);
        for (int i = 0; i < userId.size(); i++) {
            messagingTemplate.convertAndSendToUser(
                    userId.get(i).toString(),
                    "/notifications",
                    notification
            );
        }
    }
}
