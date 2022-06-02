package com.thejerryuc.theletterbox.services;


import com.thejerryuc.theletterbox.models.Message;
import com.thejerryuc.theletterbox.models.Notification;
import com.thejerryuc.theletterbox.repositories.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class NotificationServiceImpl implements NotificationService{

    @Autowired
    private NotificationRepo notificationRepo;

    @Override
    public Notification createANotificationForMessages(String forWho, Message message) {
        Notification notification = new Notification();
        if(Objects.equals(forWho, "FOR_SENDER"))
            notification = notificationRepo.save(new Notification("Your mail has been sent to "+ message.getReceiverEmail(), message.getMessageId(), LocalDateTime.now()));
        else if(Objects.equals(forWho, "FOR_RECEIVER"))
            notification = notificationRepo.save(new Notification("You have a new message in your inbox from "+ message.getSenderEmail(), message.getMessageId(), LocalDateTime.now()));
        return notification;
    }

    @Override
    public int getContainerSize() {
        return (int) notificationRepo.count();
    }

    @Override
    public Notification createAMessageDeletionNotification(String message_has_been_deleted_successfully) {
        Notification notification = new Notification();
        notification.setTimeStamp(LocalDateTime.now());
        notification.setTitle("Message has been deleted successfully");
        notificationRepo.save(notification);
        return notification;
    }

    @Override
    public Notification saveNotification(Notification senderNotification) {
        return notificationRepo.save(senderNotification);
    }
}
