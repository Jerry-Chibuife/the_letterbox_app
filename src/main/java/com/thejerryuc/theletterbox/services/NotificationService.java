package com.thejerryuc.theletterbox.services;

import com.thejerryuc.theletterbox.models.Message;
import com.thejerryuc.theletterbox.models.Notification;

public interface NotificationService {
    Notification createANotificationForMessages(String forWho, Message message);

    int getContainerSize();

    Notification createAMessageDeletionNotification(String message_has_been_deleted_successfully);

    Notification saveNotification(Notification senderNotification);
}
