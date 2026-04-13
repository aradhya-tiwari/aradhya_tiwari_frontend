package com.nucleusTeqJava2.demo.component;

import org.springframework.stereotype.Component;

@Component
public class NotificationComponent {

    public String generateMessage(String eventType, String entityName) {
        // ensure every event type is case insensitive by converting to lowercase
        if (eventType.toLowerCase().equals("created")) {
            return entityName + " has been created successfully";
        } else if (eventType.toLowerCase().equals("updated")) {
            return entityName + " has been updated successfully";
        } else if (eventType.toLowerCase().equals("deleted")) {
            return entityName + " has been deleted successfully";
        }
        return "Action performed on " + entityName;
    }

    public String sendNotification(String type, String message) {
        System.out.println(type.toUpperCase() + ":  " + message);
        return "Notification sent via " + type;
    }
}
