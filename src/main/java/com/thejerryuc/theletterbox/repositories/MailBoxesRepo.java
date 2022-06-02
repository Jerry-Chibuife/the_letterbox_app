package com.thejerryuc.theletterbox.repositories;

import com.thejerryuc.theletterbox.models.MailBoxes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MailBoxesRepo extends MongoRepository<MailBoxes, String> {
}
