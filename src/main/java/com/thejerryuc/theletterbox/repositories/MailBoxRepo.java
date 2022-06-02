package com.thejerryuc.theletterbox.repositories;

import com.thejerryuc.theletterbox.models.BoxType;
import com.thejerryuc.theletterbox.models.MailBox;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MailBoxRepo extends MongoRepository<MailBox, String> {
    void deleteMailBoxByBoxType(BoxType sent);

    MailBox findMailBoxByOwnerIdAndBoxType(String mailBoxOwnerId, BoxType inbox);
}
