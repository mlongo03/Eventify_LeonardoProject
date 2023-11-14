package com.eventify.app.controller.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventify.app.model.Notification;
import com.eventify.app.model.json.NotificationForm;
import com.eventify.app.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/show-notification/{userId}")
    public ResponseEntity<List<NotificationForm>> ShowNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.showUserNotification(userId);
        List<NotificationForm> notificheUser = new ArrayList<>();

        if (!notifications.isEmpty()) {
            for (Notification notification : notifications) {

                NotificationForm notificForm = new NotificationForm(notification.getMessage(), notification.getDateTime(), notification.getIsRead());

                notificheUser.add(notificForm);
            }
            return ResponseEntity.ok(notificheUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/setNotificationRead/{userId}")
    public ResponseEntity<String> SetNotificationsAsRead(@PathVariable Long userId) {

        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);

        for (Notification notification: notifications) {
            if (notification.getIsRead() == false) {
                notification.setIsRead(true);
                notificationService.updateNotification(notification);
            }
        }

        return ResponseEntity.ok("Notifications set correctly");
    }

}
