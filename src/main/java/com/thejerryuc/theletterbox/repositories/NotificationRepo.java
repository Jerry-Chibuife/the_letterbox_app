package com.thejerryuc.theletterbox.repositories;

import com.thejerryuc.theletterbox.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepo extends MongoRepository<Notification, String> {
}
