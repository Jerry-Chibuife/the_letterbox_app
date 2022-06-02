package com.thejerryuc.theletterbox.services;

import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.models.BoxType;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.MailBoxes;
import com.thejerryuc.theletterbox.models.Message;
import com.thejerryuc.theletterbox.repositories.MailBoxRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class MailBoxServiceImpl implements MailBoxService{

    @Autowired
    private MailBoxRepo mailBoxRepo;

    @Autowired
    private MessageService messageService;


    @Override
    public void createIndividualMailBoxesForUser(MailBoxes mailBoxes) {
        mailBoxRepo.saveAll(mailBoxes.getMailBoxList());
    }

    @Override
    public String sendMessageToSentBox(MailBox sentBox, Message message) {
        String messageId = messageService.saveMessageToRepo(message);
        sentBox.getMessageList().add(messageId);
        mailBoxRepo.save(sentBox);
        return messageId;
    }

    @Override
    public void sendMessageReferenceToInbox(List<MailBox> inboxes, String messageId) {
        for (MailBox inbox : inboxes){
            inbox.getMessageList().add(messageId);
            mailBoxRepo.save(inbox);
        }
    }

    @Override
    public Collection<? extends Message> retrieveMessagesRelatingTo(MessageRetrievalRequest messageRetrievalRequest, String boxType) {
        List<Message> messageList = new ArrayList<>();
        MailBox box;
        if(Objects.equals(boxType, BoxType.INBOX.toString().toLowerCase(Locale.ROOT)))
            box = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(messageRetrievalRequest.getMailBoxOwnerId(), BoxType.INBOX);
        else box = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(messageRetrievalRequest.getMailBoxOwnerId(), BoxType.SENT);
        for (String messageId : box.getMessageList()){
            if(messageService.containsMessageRelatingTo(messageId, messageRetrievalRequest)){
                messageService.markAsRead(messageId);
                messageList.add(messageService.retrieveMessageOfId(messageId));
            }
        }
        return messageList;
    }

    @Override
    public Message retrieveMessageFromInboxUsing(String messageId) {
        Message message = messageService.retrieveMessageOfId(messageId);
        MailBox inbox = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(message.getReceiverEmail().get(0), BoxType.INBOX);
        if(inbox.getMessageList().contains(messageId))
            return messageService.retrieveMessageOfId(messageId);
        throw new TheLetterBoxAppException("Message not in user inbox");
    }

    @Override
    public boolean deleteMessageFromUserInbox(String messageId) {
        Message message = messageService.retrieveMessageOfId(messageId);
        MailBox inbox = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(message.getReceiverEmail().get(0), BoxType.INBOX);
        for (String storedId : inbox.getMessageList()){
            if(Objects.equals(storedId, messageId)){
                inbox.getMessageList().remove(storedId);
                mailBoxRepo.save(inbox);
                return true;
            }
        }
        return false;
    }

    @Override
    public Message retrieveMessageFromSentBoxUsing(String messageId) {
        Message message = messageService.retrieveMessageOfId(messageId);
        MailBox sentBox = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(message.getSenderEmail(), BoxType.SENT);
        if(sentBox.getMessageList().contains(messageId))
            return messageService.retrieveMessageOfId(messageId);
        throw new TheLetterBoxAppException("Message not in user sent box");
    }

    @Override
    public boolean deleteMessageFromUserSentBox(String messageId) {
        Message message = messageService.retrieveMessageOfId(messageId);
        MailBox inbox = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(message.getSenderEmail(), BoxType.SENT);
        for (String storedId : inbox.getMessageList()){
            if(Objects.equals(storedId, messageId)){
                inbox.getMessageList().remove(storedId);
                mailBoxRepo.save(inbox);
                return true;
            }
        }
        return false;
    }

    @Override
    public String forwardMessageToAnotherUser(String senderEmail, String messageId, List<String> receiverEmails) {
        String newMessageId = null;
        MailBox senderInbox = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(senderEmail, BoxType.INBOX);
        MailBox senderSentBox = mailBoxRepo.findMailBoxByOwnerIdAndBoxType(senderEmail, BoxType.SENT);
        List<MailBox> receiverInboxes = new ArrayList<>();
        for (String receiverEmail : receiverEmails){
            receiverInboxes.add(mailBoxRepo.findMailBoxByOwnerIdAndBoxType(receiverEmail, BoxType.INBOX));
        }

        if(senderInbox.getMessageList().contains(messageId)){
            Message message = messageService.retrieveMessageOfId(messageId);
            message.setMessageId(null);
            message.setReceiverEmail(new ArrayList<>());
            message.getReceiverEmail().addAll(receiverEmails);
            message.setCreationTime(LocalDateTime.now());
            message.setSenderEmail(senderEmail);
            newMessageId = messageService.saveMessageToRepo(message);
            senderSentBox.getMessageList().add(newMessageId);
            for (MailBox receiverInbox : receiverInboxes){
                receiverInbox.getMessageList().add(newMessageId);
            }

            mailBoxRepo.save(senderSentBox);
            mailBoxRepo.saveAll(receiverInboxes);
        }
        else if(senderSentBox.getMessageList().contains(messageId)){
            Message message = messageService.retrieveMessageOfId(messageId);
            message.setMessageId(null);
            message.setReceiverEmail(new ArrayList<>());
            message.getReceiverEmail().addAll(receiverEmails);
            message.setCreationTime(LocalDateTime.now());
            message.setSenderEmail(senderEmail);
            newMessageId = messageService.saveMessageToRepo(message);
            senderSentBox.getMessageList().add(newMessageId);
            for (MailBox receiverInbox : receiverInboxes){
                receiverInbox.getMessageList().add(newMessageId);
            }
            mailBoxRepo.save(senderSentBox);
            mailBoxRepo.saveAll(receiverInboxes);
        }
        return newMessageId;
    }

    @Override
    public void markMessagesInInboxAsDeleivered(List<String> messageList) {
        for (String messageId : messageList){
            messageService.markAsDelivered(messageId);
        }
    }
}
