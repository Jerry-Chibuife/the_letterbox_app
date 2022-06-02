package com.thejerryuc.theletterbox.services;

import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;

import java.util.List;

public interface MessageService {

    String saveMessageToRepo(Message message);

    boolean containsMessageRelatingTo(String messageId, MessageRetrievalRequest messageRetrievalRequest);

    Message retrieveMessageOfId(String messageId);

    void markAsDelivered(String messageId);

    void markAsRead(String messageId);
}
