package com.thejerryuc.theletterbox.services;



import com.thejerryuc.theletterbox.dtos.requests.AccountCreationRequest;
import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.dtos.responses.AccountCreationResponse;
import com.thejerryuc.theletterbox.dtos.responses.UserDto;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.models.*;
import com.thejerryuc.theletterbox.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailBoxesService mailBoxesService;

    @Autowired
    private NotificationService notificationService;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public AccountCreationResponse registerUser(AccountCreationRequest request) {
        Optional<User> optionalUser = userRepo.findById(request.getEmail());
        if(optionalUser.isPresent()){
            throw new TheLetterBoxAppException("User already exists");
        }
        User user = new User();
        modelMapper.map(request, user);
        user.setNotifications(new ArrayList<>());
        User savedUser = userRepo.save(user);
        mailBoxesService.createUserMailBoxes(savedUser.getEmail());
        AccountCreationResponse response = new AccountCreationResponse();
        modelMapper.map(savedUser, response);
        return response;
    }

    @Override
    public UserDto retrieveUser(String email) {
        User user = userRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        UserDto userDto = new UserDto();
        modelMapper.map(user, userDto);
        return userDto;
    }

    @Override
    public void logUserIn(String email, String password) {
        User user = userRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        if(Objects.equals(user.getPassword(), password)){
            user.setLoggedIn(true);
            userRepo.save(user);
        }
    }

    @Override
    public MailBoxes retrieveUserMailBoxes(String email) {
        User user = userRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        if(!user.isLoggedIn()){
            throw new TheLetterBoxAppException("User is not logged in");
        }
        return mailBoxesService.retrieveUserMailBoxes(email);
    }

    @Override
    public MailBox retrieveUserInboxFromMailBox(String email) {
        User user = userRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        if(!user.isLoggedIn()){
            throw new TheLetterBoxAppException("User is not logged in");
        }
        MailBoxes mailBoxes = mailBoxesService.retrieveUserMailBoxes(email);
        return mailBoxesService.retrieveUserInbox(mailBoxes);
    }

    @Override
    public void sendMessageToUser(Message message) {
        User sender = userRepo.findById(message.getSenderEmail()).orElseThrow(()-> new TheLetterBoxAppException("Sender does not exist"));
        if(!sender.isLoggedIn()){
            throw new TheLetterBoxAppException("Sender is not logged in");
        }

        User theSender = userRepo.findById(message.getSenderEmail()).get();
        String messageId = mailBoxesService.sendMessageToSenderSentBox(message);
        Notification senderNotification = Notification.builder()
                .messageId(messageId)
                .timeStamp(LocalDateTime.now())
                .title("Your message has been sent")
                .build();
        notificationService.saveNotification(senderNotification);
        theSender.getNotifications().add(senderNotification);
        userRepo.save(theSender);

        List<String> receiverEmails = new ArrayList<>(message.getReceiverEmail());
        mailBoxesService.sendMessageReferenceToReceiverInbox(receiverEmails, messageId);
        Notification receiverNotification = Notification.builder()
                .messageId(messageId)
                .timeStamp(LocalDateTime.now())
                .title("A message has been sent to your inbox")
                .build();
        notificationService.saveNotification(receiverNotification);
        for (String receiverEmail : receiverEmails){
            User theReceiver = userRepo.findById(receiverEmail).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
            theReceiver.getNotifications().add(receiverNotification);
            userRepo.save(theReceiver);
        }
    }

    @Override
    public List<Message> retrieveMessageFromUserInbox(MessageRetrievalRequest messageRetrievalRequest) {
        return new ArrayList<>(mailBoxesService.retrieveMessagesFromInboxRelatingTo(messageRetrievalRequest));
    }

    @Override
    public List<Message> retrieveMessageFromUserSentBox(MessageRetrievalRequest messageRetrievalRequest) {
        return new ArrayList<>(mailBoxesService.retrieveMessagesFromSentBoxRelatingTo(messageRetrievalRequest));
    }

    @Override
    public Message retrieveMessageFromUserSentBox(String messageId) {
        return mailBoxesService.retrieveMessageFromSentBoxWith(messageId);
    }

    @Override
    public String deleteMessageFromInbox(String messageId) {
        boolean isDeleted = mailBoxesService.deleteMessageFromUserInbox(messageId);
        if (isDeleted){
            return "Message has been deleted successfully";
        }
        return null;
    }

    @Override
    public Message retrieveMessageFromUserInbox(String messageId) {
        return mailBoxesService.retrieveMessageFromInboxWith(messageId);
    }

    @Override
    public String deleteMessageFromSentBox(String messageId) {
        boolean isDeleted = mailBoxesService.deleteMessageFromUserSentBox(messageId);
        if (isDeleted){
            return "Message has been deleted successfully";
        }
        return null;
    }

    @Override
    public void forwardMessageToAnotherReceiver(String sender, String messageId, List<String> receivers) {
        User senderSaved = userRepo.findById(sender).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        List<User> receiversSaved = (List<User>) userRepo.findAllById(receivers);
        List<String> receiverEmails = new ArrayList<>();
        for (User user : receiversSaved){
            receiverEmails.add(user.getEmail());
        }

        String newMessageId = mailBoxesService.forwardMessageToAnotherUser(sender, messageId, receiverEmails);

        Notification senderNotification = Notification.builder()
                .messageId(newMessageId)
                .timeStamp(LocalDateTime.now())
                .title("Your message has been sent")
                .build();
        senderSaved.getNotifications().add(notificationService.saveNotification(senderNotification));
        userRepo.save(senderSaved);

        Notification receiverNotification = Notification.builder()
                .messageId(newMessageId)
                .timeStamp(LocalDateTime.now())
                .title("You have a new message in your inbox")
                .build();
        for (User receiver : receiversSaved){
            receiver.getNotifications().add(notificationService.saveNotification(receiverNotification));
            userRepo.save(receiver);
        }

    }

    @Override
    public MailBox retrieveUserSentBoxFromMailBox(String email) {
        User user = userRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        if(!user.isLoggedIn()){
            throw new TheLetterBoxAppException("User is not logged in");
        }
        MailBoxes mailBoxes = mailBoxesService.retrieveUserMailBoxes(email);
        return mailBoxesService.retrieveUserSentBox(mailBoxes);
    }

    @Override
    public void deleteNotificationFromUserList(String email, String id) {
        User user = userRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        user.setNotifications(user.getNotifications().stream().filter(notification -> !Objects.equals(notification.getId(), id)).collect(Collectors.toList()));
        userRepo.save(user);
    }

    @Override
    public void logUserOut(String userEmail, String confirmation) {
        User user = userRepo.findById(userEmail).orElseThrow(()-> new TheLetterBoxAppException("User not found"));
        user.setLoggedIn(false);
        userRepo.save(user);
    }
}
