package com.thejerryuc.theletterbox.services;

import com.thejerryuc.theletterbox.dtos.requests.MessageRetrievalRequest;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.models.MailBox;
import com.thejerryuc.theletterbox.models.Message;
import com.thejerryuc.theletterbox.repositories.MessagesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    private MessagesRepo messagesRepo;


    @Override
    public String saveMessageToRepo(Message message) {
        Message savedMessage = messagesRepo.save(message);
        return savedMessage.getMessageId();
    }

    @Override
    public boolean containsMessageRelatingTo(String messageId, MessageRetrievalRequest messageRetrievalRequest) {
        Optional<Message> message = messagesRepo.findById(messageId);
        if(message.isPresent()){
            if(Objects.equals(message.get().getSubject(), messageRetrievalRequest.getMailSubject()) && messageRetrievalRequest.getMailSubject() != null)
                if(Objects.equals(message.get().getContent(), messageRetrievalRequest.getMailContent()) && messageRetrievalRequest.getMailContent() != null)
                    return Objects.equals(message.get().getSenderEmail(), messageRetrievalRequest.getMailSender()) && messageRetrievalRequest.getMailSender() != null;
        }
        return false;
    }

    @Override
    public Message retrieveMessageOfId(String messageId) {
        return messagesRepo.findById(messageId).get();
    }

    @Override
    public void markAsDelivered(String messageId) {
        Optional<Message> message = messagesRepo.findById(messageId);
        if(message.isPresent()){
            message.get().setDelivered(true);
            messagesRepo.save(message.get());
        }
    }

    @Override
    public void markAsRead(String messageId) {
        Optional<Message> message = messagesRepo.findById(messageId);
        if(message.isPresent()){
            message.get().setDelivered(true);
            message.get().setRead(true);
            messagesRepo.save(message.get());
        }
    }
}
