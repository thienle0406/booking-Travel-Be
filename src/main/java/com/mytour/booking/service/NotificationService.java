package com.mytour.booking.service;

import com.mytour.booking.model.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Gui thong bao cho tat ca Admin (subscribe /topic/admin/notifications)
     */
    public void notifyAdmins(String type, String title, String message, String bookingId) {
        NotificationMessage notification = new NotificationMessage(
                type, title, message, bookingId, "ADMIN", null,
                System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/admin/notifications", notification);
    }

    /**
     * Gui thong bao cho 1 tai xe cu the (subscribe /topic/driver/{driverId})
     */
    public void notifyDriver(String driverId, String title, String message, String bookingId) {
        NotificationMessage notification = new NotificationMessage(
                "DRIVER_ASSIGNED", title, message, bookingId, "DRIVER", driverId,
                System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/driver/" + driverId, notification);
    }

    /**
     * Gui thong bao cho 1 user cu the (subscribe /topic/user/{userId})
     */
    public void notifyUser(Long userId, String type, String title, String message, String bookingId) {
        NotificationMessage notification = new NotificationMessage(
                type, title, message, bookingId, "USER", String.valueOf(userId),
                System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/user/" + userId, notification);
    }
}