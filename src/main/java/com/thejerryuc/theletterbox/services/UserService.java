package com.thejerryuc.theletterbox.services;


import com.thejerryuc.theletterbox.dtos.requests.AccountCreationRequest;
import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.dtos.responses.AccountCreationResponse;
import com.thejerryuc.theletterbox.dtos.responses.UserDto;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;

import java.util.List;

public interface UserService {

    AccountCreationResponse registerUser(AccountCreationRequest request);

    UserDto retrieveUser(String email);

    void logUserIn(String email, String password);

    MailBoxes retrieveUserMailBoxes(String email);

    MailBox retrieveUserInboxFromMailBox(String email);

    void sendMessageToUser(Message message);

    List<Message> retrieveMessageFromUserInbox(MessageRetrievalRequest messageRetrievalRequest);

    List<Message> retrieveMessageFromUserSentBox(MessageRetrievalRequest messageRetrievalRequest);

    Message retrieveMessageFromUserSentBox(String messageId);

    String deleteMessageFromInbox(String messageId);

    Message retrieveMessageFromUserInbox(String messageId);

    String deleteMessageFromSentBox(String messageId);

    void forwardMessageToAnotherReceiver(String sender, String messageId, List<String> receivers);

    MailBox retrieveUserSentBoxFromMailBox(String email);

    void deleteNotificationFromUserList(String email, String id);

    void logUserOut(String userEmail, String confirmation);
}
