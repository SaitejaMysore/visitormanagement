package com.visitormanagement.service;

import com.visitormanagement.entity.Notification;
import com.visitormanagement.entity.User;
import com.visitormanagement.entity.Visitor;
import com.visitormanagement.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification createNotification(User user, Visitor visitor, String message) {
        Notification notification = new Notification(user, visitor, message);
        return notificationRepository.save(notification);
    }
    
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Notification> findUnreadByUser(User user) {
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }
    
    public long countUnreadByUser(User user) {
        return notificationRepository.countUnreadByUser(user);
    }
    
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = findUnreadByUser(user);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
}