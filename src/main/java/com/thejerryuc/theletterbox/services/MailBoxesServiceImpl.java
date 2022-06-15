package com.thejerryuc.theletterbox.services;


import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.models.BoxType;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;
import com.thejerryuc.theletterbox.repositories.MailBoxesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MailBoxesServiceImpl implements MailBoxesService{

    @Autowired
    private MailBoxesRepo mailBoxesRepo;

    @Autowired
    private MailBoxService mailBoxService;

    @Override
    public void createUserMailBoxes(String email) {
        MailBoxes mailBoxes = new MailBoxes(email);
        mailBoxService.createIndividualMailBoxesForUser(mailBoxes);
        mailBoxesRepo.save(mailBoxes);
    }

    @Override
    public MailBoxes retrieveUserMailBoxes(String email) {
        return mailBoxesRepo.findById(email).orElseThrow(()-> new TheLetterBoxAppException("User does not have mailboxes"));
    }

    @Override
    public MailBox retrieveUserInbox(MailBoxes mailBoxes) {
        MailBox inbox = new MailBox();
        for (MailBox mailBox : mailBoxes.getMailBoxList()){
            if(mailBox.getBoxType() == BoxType.INBOX){
                mailBoxService.markMessagesInInboxAsDeleivered(mailBox.getMessageList());
                inbox = mailBox;
            }
        }
        return inbox;
    }

    @Override
    public String sendMessageToSenderSentBox(Message message) {
        Optional<MailBoxes> mailBoxes = mailBoxesRepo.findById(message.getSenderEmail());
        MailBox sentBox = mailBoxes.get().getMailBoxList().get(1);
        String messageId = mailBoxService.sendMessageToSentBox(sentBox, message);
        mailBoxesRepo.save(mailBoxes.get());
        return messageId;
    }

    @Override
    public void sendMessageReferenceToReceiverInbox(List<String> receiverEmail, String messageId) {
        List<MailBoxes> mailBoxes = (List<MailBoxes>) mailBoxesRepo.findAllById(receiverEmail);
        List<MailBox> inboxes = new ArrayList<>();
        for (MailBoxes mailBoxes1 : mailBoxes){
            inboxes.add(mailBoxes1.getMailBoxList().get(0));
        }
        mailBoxService.sendMessageReferenceToInbox(inboxes, messageId);
        mailBoxesRepo.saveAll(mailBoxes);
    }

    @Override
    public Collection<? extends Message> retrieveMessagesFromSentBoxRelatingTo(MessageRetrievalRequest messageRetrievalRequest) {
        return mailBoxService.retrieveMessagesRelatingTo(messageRetrievalRequest, "sent");
    }

    @Override
    public Message retrieveMessageFromInboxWith(String messageId) {
        Message message = mailBoxService.retrieveMessageFromInboxUsing(messageId);
        if(Objects.equals(message.getMessageId(), messageId)){
            return message;
        }
        throw new TheLetterBoxAppException("Wrong message");
    }

    @Override
    public boolean deleteMessageFromUserInbox(String messageId) {
        return mailBoxService.deleteMessageFromUserInbox(messageId);
    }

    @Override
    public Message retrieveMessageFromSentBoxWith(String messageId) {
        Message message = mailBoxService.retrieveMessageFromSentBoxUsing(messageId);
        if(Objects.equals(message.getMessageId(), messageId)){
            return message;
        }
        throw new TheLetterBoxAppException("Wrong message");
    }

    @Override
    public boolean deleteMessageFromUserSentBox(String messageId) {
        return mailBoxService.deleteMessageFromUserSentBox(messageId);
    }

    @Override
    public String forwardMessageToAnotherUser(String senderEmail, String messageId, List<String> receiverEmails) {
        return mailBoxService.forwardMessageToAnotherUser(senderEmail, messageId, receiverEmails);
    }

    @Override
    public MailBox retrieveUserSentBox(MailBoxes mailBoxes) {
        MailBox sentBox = new MailBox();
        for (MailBox mailBox : mailBoxes.getMailBoxList()){
            if(mailBox.getBoxType() == BoxType.SENT){
                sentBox = mailBox;
            }
        }
        return sentBox;
    }

    @Override
    public Collection<? extends Message> retrieveMessagesFromInboxRelatingTo( MessageRetrievalRequest messageRetrievalRequest) {
        return mailBoxService.retrieveMessagesRelatingTo(messageRetrievalRequest, "inbox");
    }
}
