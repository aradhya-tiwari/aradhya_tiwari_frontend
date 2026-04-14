package com.nucleusTeqJava2.demo.service;

import com.nucleusTeqJava2.demo.component.NotificationComponent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private NotificationComponent notificationComponent;

    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    // type can be EMAIL, SMS, PUSH
    public String triggerNotification(String notificationType, String eventType, String entityName) {
        String message = notificationComponent.generateMessage(eventType, entityName);
        return notificationComponent.sendNotification(notificationType, message);
    }

    public String sendNotification(String notificationType, String customMessage) {
        return notificationComponent.sendNotification(notificationType, customMessage);
    }
}
