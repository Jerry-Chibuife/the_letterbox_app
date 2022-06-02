package com.thejerryuc.theletterbox.repositories;

import com.thejerryuc.theletterbox.models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessagesRepo extends MongoRepository<Message, String> {
}
