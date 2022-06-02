package com.thejerryuc.theletterbox.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailBoxes {

    @Id
    private String ownerId;

    private List<MailBox> mailBoxList;

    public MailBoxes(String ownerId) {
        this.ownerId = ownerId;
        if(mailBoxList == null){
            mailBoxList = new ArrayList<>();
        }
        mailBoxList.add(new MailBox(ownerId, BoxType.INBOX));
        mailBoxList.add(new MailBox(ownerId, BoxType.SENT));
    }
}
