package com.thejerryuc.theletterbox.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    private String messageId;
    private String senderEmail;
    private List<String> receiverEmail;
    private String subject;
    private String content;
    private boolean isRead;
    private boolean isDelivered;
    private LocalDateTime creationTime;

    public List<String> getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(List<String> receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    //    private boolean canBeViewedBySender = true;
//    private boolean canBeViewedByReceiver = true;

}
