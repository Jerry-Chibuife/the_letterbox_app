package com.thejerryuc.theletterbox.services;


import com.thejerryuc.theletterbox.dtos.requests.AccountCreationRequest;
import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.dtos.responses.AccountCreationResponse;
import com.thejerryuc.theletterbox.dtos.responses.UserDto;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MailBoxesService mailBoxesService;

    @Autowired
    private NotificationService notificationService;

    AccountCreationRequest request;

    @BeforeEach
    void beforeEachTest(){
         request = AccountCreationRequest.builder()
                .email("john@mail.com")
                .firstName("John")
                .middleName("Tolu")
                .lastName("Adeola")
                .password("contest123")
                .build();
    }


    @Test
    void userCanBeRegistered(){
        AccountCreationResponse response = userService.registerUser(request);
        userService.logUserIn(request.getEmail(), request.getPassword());
        UserDto userDto = userService.retrieveUser(response.getEmail());
        assertEquals(userDto.getFirstName(), "John");
    }

    @Test
    void userCanBeLoggedIn(){
        userService.registerUser(request);
        userService.logUserIn(request.getEmail(), request.getPassword());
        UserDto userDto = userService.retrieveUser(request.getEmail());
        assertTrue(userDto.isLoggedIn());
    }

    @Test
    void userMailBoxesAreCreatedAtMomentOfRegistration(){
        userService.registerUser(request);
        userService.logUserIn(request.getEmail(), request.getPassword());
        MailBoxes mailBoxes = userService.retrieveUserMailBoxes(request.getEmail());
        assertEquals(request.getEmail(), mailBoxes.getOwnerId());
        assertEquals(2, mailBoxes.getMailBoxList().size());
    }

    @Test
    void userIndividualBoxesAreCreatedAtRegistration(){
        userService.registerUser(request);
        userService.logUserIn(request.getEmail(), request.getPassword());
        MailBox inbox = userService.retrieveUserInboxFromMailBox(request.getEmail());
        assertEquals(request.getEmail(), inbox.getOwnerId());
    }

    @Test
    void userIndividualBoxesHaveMessageFolderSpacesAtRegistration(){
        userService.registerUser(request);
        userService.logUserIn(request.getEmail(), request.getPassword());
        MailBox inbox = userService.retrieveUserInboxFromMailBox(request.getEmail());
        assertNotNull(inbox.getMessageList());
    }

    @Test
    void userCanNotPerformActionsWithoutLoggingIn(){
        userService.registerUser(request);
        assertThrows(TheLetterBoxAppException.class, ()-> userService.retrieveUserInboxFromMailBox(request.getEmail()));
    }

    @Test
    void userCanSendAMessageToAnotherUser(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        MailBox inbox = userService.retrieveUserInboxFromMailBox(request2.getEmail());
        assertEquals(1, inbox.getMessageList().size());
    }

    @Test
    void usersGetNotificationsWhenMessageIsSent(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        UserDto userDto1 = userService.retrieveUser(request.getEmail());
        assertEquals(1, userDto1.getNotifications().size());
        UserDto userDto2 = userService.retrieveUser(request2.getEmail());
        assertEquals(1, userDto2.getNotifications().size());
    }

    @Test
    void userCanRetrieveAMessageToView(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        MessageRetrievalRequest messageRetrievalRequest = new MessageRetrievalRequest(request2.getEmail(), message.getSenderEmail(), message.getContent(), message.getSubject());
        List<Message> inboxMessages = userService.retrieveMessageFromUserInboxContaining(messageRetrievalRequest);
        assertEquals(1, inboxMessages.size());
        assertEquals("Test Message", inboxMessages.get(0).getSubject());
        messageRetrievalRequest = new MessageRetrievalRequest(request.getEmail(), message.getSenderEmail(), message.getContent(), message.getSubject());
        List<Message> sentBoxMessages = userService.retrieveMessageFromUserSentBoxContaining(messageRetrievalRequest);
        assertEquals(1, sentBoxMessages.size());
        assertEquals("Test Message", sentBoxMessages.get(0).getSubject());
    }

    @Test
    void userCanRetrieveMessageById(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        UserDto userDto1 = userService.retrieveUser(request.getEmail());
        Message message1 = userService.retrieveMessageFromUserSentBox(userDto1.getNotifications().get(0).getMessageId());
        assertEquals("Test Message", message1.getSubject());
    }

    @Test
    void userCanDeleteMessage(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        UserDto userDto1 = userService.retrieveUser(request.getEmail());
        Message message1 = userService.retrieveMessageFromUserSentBox(userDto1.getNotifications().get(0).getMessageId());
        userService.deleteMessageFromSentBox(message1.getMessageId());
        UserDto userDto2 = userService.retrieveUser(request.getEmail());
        assertThrows(TheLetterBoxAppException.class, ()-> userService.retrieveMessageFromUserSentBox(userDto2.getNotifications().get(0).getMessageId()));
    }

    @Test
    void userCanForwardMessageToAnotherUser(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        UserDto userDto1 = userService.retrieveUser(request.getEmail());
        Message message1 = userService.retrieveMessageFromUserSentBox(userDto1.getNotifications().get(0).getMessageId());

        AccountCreationRequest request3 = AccountCreationRequest.builder()
                .email("ben@mail.com")
                .firstName("Benedict")
                .middleName("Asha")
                .lastName("Caine")
                .password("barbie098")
                .build();
        userService.registerUser(request3);
        userService.logUserIn(request3.getEmail(), request3.getPassword());
        List<String> receivers = new ArrayList<>();
        receivers.add(request3.getEmail());
        userService.forwardMessageToAnotherReceiver(request2.getEmail(), message1.getMessageId(), receivers);

        UserDto userDto = userService.retrieveUser(request3.getEmail());
        Message message2 = userService.retrieveMessageFromUserInbox(userDto.getNotifications().get(0).getMessageId());
        assertEquals("sarah@mail.com", message2.getSenderEmail());
    }

    @Test
    void userCanSendMessageToMultipleUsers(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        AccountCreationRequest request3 = AccountCreationRequest.builder()
                .email("ben@mail.com")
                .firstName("Benedict")
                .middleName("Asha")
                .lastName("Caine")
                .password("barbie098")
                .build();
        userService.registerUser(request3);
        userService.logUserIn(request3.getEmail(), request3.getPassword());

        MailBox beforeMessageInbox = userService.retrieveUserInboxFromMailBox(request3.getEmail());
        assertEquals(0, beforeMessageInbox.getMessageList().size());

        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        message.getReceiverEmail().add(request3.getEmail());
        userService.sendMessageToUser(message);

        MailBox afterMessageSentInbox = userService.retrieveUserInboxFromMailBox(request3.getEmail());
        assertEquals(1, afterMessageSentInbox.getMessageList().size());
    }

    @Test
    void userCanDeleteNotification(){
        userService.registerUser(request);
        AccountCreationRequest request2 = AccountCreationRequest.builder()
                .email("sarah@mail.com")
                .firstName("Sarah")
                .middleName("Temi")
                .lastName("Adams")
                .password("barbie098")
                .build();
        userService.registerUser(request2);
        userService.logUserIn(request.getEmail(), request.getPassword());
        Message message = Message.builder()
                .content("This is the first message")
                .senderEmail(request.getEmail())
                .receiverEmail(new ArrayList<>())
                .subject("Test Message")
                .creationTime(LocalDateTime.now())
                .build();
        message.getReceiverEmail().add(request2.getEmail());
        userService.sendMessageToUser(message);
        userService.logUserIn(request2.getEmail(), request2.getPassword());
        UserDto userDto1 = userService.retrieveUser(request.getEmail());
        assertEquals(1, userDto1.getNotifications().size());
        userService.deleteNotificationFromUserList(userDto1.getEmail(), userDto1.getNotifications().get(0).getId());
        UserDto userDto2 = userService.retrieveUser(request.getEmail());
        assertEquals(0, userDto2.getNotifications().size());
    }

    @Test
    void userCanLogOut(){
        userService.registerUser(request);
        UserDto userDto1 = userService.retrieveUser(request.getEmail());
        assertFalse(userDto1.isLoggedIn());
        userService.logUserIn(request.getEmail(), request.getPassword());
        UserDto userDto2 = userService.retrieveUser(request.getEmail());
        assertTrue(userDto2.isLoggedIn());
        userService.logUserOut(userDto2.getEmail(), "Y");
        UserDto userDto3 = userService.retrieveUser(request.getEmail());
        assertFalse(userDto3.isLoggedIn());
    }

}