package com.thejerryuc.theletterbox.services;

import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;

import java.util.Collection;
import java.util.List;

public interface MailBoxService {

    void createIndividualMailBoxesForUser(MailBoxes mailBoxes);

    String sendMessageToSentBox(MailBox sentBox, Message message);

    void sendMessageReferenceToInbox(List<MailBox> inbox, String messageId);

    Collection<? extends Message> retrieveMessagesRelatingTo(MessageRetrievalRequest messageRetrievalRequest, String boxType);

    Message retrieveMessageFromInboxUsing(String messageId);

    boolean deleteMessageFromUserInbox(String messageId);

    Message retrieveMessageFromSentBoxUsing(String messageId);

    boolean deleteMessageFromUserSentBox(String messageId);

    String forwardMessageToAnotherUser(String senderEmail, String messageId, List<String> receiverEmail);

    void markMessagesInInboxAsDeleivered(List<String> messageList);
}
