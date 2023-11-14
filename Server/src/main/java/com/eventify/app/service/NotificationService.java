package com.eventify.app.service;

import com.eventify.app.model.Event;
import com.eventify.app.model.Notification;
import com.eventify.app.model.User;
import com.eventify.app.model.json.NotificationForm;
import com.eventify.app.repository.INotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final INotificationRepository notificationRepository;
    private final UserService userService;
    private final EventService eventService;

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification updateNotification(Notification event) {
        return notificationRepository.save(event);
    }

    public void deleteNotificationById(Long id) {
        notificationRepository.deleteById(id);
    }

    public void createNotification(Long userId, Long eventId, String message, String title) {

        User user = userService.getById(userId).get();
        Event event = null;
        if (eventId != null) {
            event = eventService.getEventById(eventId).get();
        }

        if (message == "Subscribed for creator") {
            message = "The user" + user.getFirstname() + " " + user.getLastname() + " is subscribed to your event: " + event.getTitle();
            if (eventId != null) {
                user = userService.getById(event.getCreator().getId()).get();
            }
        } else if (message == "Unsubscribed for creator") {
            message = "The user" + user.getFirstname() + " " + user.getLastname() + " is unsubscribed to your event: " + event.getTitle();
            if (eventId != null) {
                user = userService.getById(event.getCreator().getId()).get();
            }
        } else {
            if (eventId != null) {
                message += event.getTitle();
            } else if (title != null) {
                message += title;
            }
        }

        Notification notification = new Notification(user, message);
        notificationRepository.save(notification);
        return ;
    }

    public List<Notification> showUserNotification(Long userId) {
        List<Notification> notifications = getAllNotifications();
        List<Notification> notificheUser = new ArrayList<>();

        for (Notification notification : notifications) {
            if (notification.getUser().getId() == userId) {
                notificheUser.add(notification);
            }
        }
        notificheUser.sort(Comparator.comparing(Notification::getDateTime).reversed());

        return notificheUser;
    }

    public boolean sendNotificationToUserId(Long userId) throws JsonProcessingException {

        List<Notification> notificationList = getAllNotifications();


        List<NotificationForm> resultNotifications = new ArrayList<>();

        for (Notification notification : notificationList) {
            if (notification.getUser().getId() == userId) {
                resultNotifications.add(NotificationForm.builder()
                .isRead(notification.getIsRead())
                .message(notification.getMessage())
                .dateTime(notification.getDateTime())
                .build());
            }
        }
        resultNotifications.sort(Comparator.comparing(NotificationForm::getDateTime).reversed());

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(resultNotifications);

        SseService.sendSseEventToClients(String.valueOf(userId), json);
        return true;
    }

    public List<Notification> getNotificationsByUserId(Long userId) {

        List<Notification> allNotifications = notificationRepository.findAll();
        List<Notification> notificationResponse = new ArrayList<>();

        for (Notification notification: allNotifications) {
            if (notification.getUser().getId() == userId) {
                notificationResponse.add(notification);
            }
        }

        return notificationResponse;
    }
}
