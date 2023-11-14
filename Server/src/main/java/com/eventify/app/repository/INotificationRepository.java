package com.eventify.app.repository;

import com.eventify.app.model.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
   List<Notification> getNotificationsByUser_Id(Long userId);
}
