package com.eventify.app.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import java.time.Duration;
import com.eventify.app.model.Event;
import com.eventify.app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventReminderScheduler.class);

    private final EmailService emailService;
    private final NotificationService notificationService;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Map<Long, ScheduledFuture<?>> scheduledReminders = new ConcurrentHashMap<>();

    public void scheduleEventReminders(Event event) {
        LocalDateTime eventStartTime = event.getDateTime();
        LocalDateTime now = LocalDateTime.now();

        Duration timeUntilEvent = Duration.between(now, eventStartTime);
        long delayMillis = timeUntilEvent.minusMinutes(30).toMillis();

        if (delayMillis > 0) {
            Runnable reminderTask = () -> {
                try {
                    sendEventReminders(event);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            };
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(reminderTask, delayMillis, TimeUnit.MILLISECONDS);

            scheduledReminders.put(event.getId(), scheduledFuture);
            logger.info("Scheduled reminder for event: {}", event.getId());
        }
    }

    public void unscheduleEventReminders(Event event) {
        Long eventId = event.getId();

        ScheduledFuture<?> scheduledFuture = scheduledReminders.get(eventId);

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledReminders.remove(eventId);

            logger.info("Unscheduled reminder for event: {}", event.getId());
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Error while shutting down EventReminderScheduler", e);
        }
    }

    private void sendEventReminders(Event event) throws JsonProcessingException {
        try {
            for (User participant : event.getParticipants()) {
                emailService.sendEventReminder(participant.getEmail(), event.getTitle());
                notificationService.createNotification(participant.getId(), event.getId(), "Ti ricordiamo che sta per cominciare l'evento a cui sei iscritto : ", null);
                notificationService.sendNotificationToUserId(participant.getId());
            }
        } catch (MessagingException e) {
            System.out.println("Error sending reminder for event: " + event.getId());
            logger.error("Error sending reminder for event: {}", event.getId(), e);
        }
    }
}
