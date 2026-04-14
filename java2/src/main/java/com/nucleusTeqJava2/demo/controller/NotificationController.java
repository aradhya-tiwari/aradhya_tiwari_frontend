package com.nucleusTeqJava2.demo.controller;

import com.nucleusTeqJava2.demo.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/trigger")
    public String triggerNotification(
            @RequestParam String notificationType,
            @RequestParam String eventType,
            @RequestParam String entityName) {
        return notificationService.triggerNotification(notificationType, eventType, entityName);
    }

    @PostMapping("/send")
    public String sendCustom(
            @RequestParam String notificationType,
            @RequestParam String message) {
        return notificationService.sendNotification(notificationType, message);
    }
}
