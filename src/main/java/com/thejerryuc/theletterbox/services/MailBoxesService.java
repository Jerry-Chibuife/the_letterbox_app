package com.thejerryuc.theletterbox.services;

import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;

import java.util.Collection;
import java.util.List;

public interface MailBoxesService {

    void createUserMailBoxes(String email);

    MailBoxes retrieveUserMailBoxes(String email);

    MailBox retrieveUserInbox(MailBoxes mailBoxes);

    String sendMessageToSenderSentBox(Message message);

    void sendMessageReferenceToReceiverInbox(List<String> receiverEmail, String messageId);

    Collection<? extends Message> retrieveMessagesFromInboxRelatingTo( MessageRetrievalRequest messageRetrievalRequest);

    Collection<? extends Message> retrieveMessagesFromSentBoxRelatingTo(MessageRetrievalRequest messageRetrievalRequest);

    Message retrieveMessageFromInboxWith(String messageId);

    boolean deleteMessageFromUserInbox(String messageId);

    Message retrieveMessageFromSentBoxWith(String messageId);

    boolean deleteMessageFromUserSentBox(String messageId);

    String forwardMessageToAnotherUser(String senderEmail, String messageId, List<String> receiverEmails);

    MailBox retrieveUserSentBox(MailBoxes mailBoxes);
}
