package com.swe.lms.notification.repository;

import com.swe.lms.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsers_Id(Long userId);
}
