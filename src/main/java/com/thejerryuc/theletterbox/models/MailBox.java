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
public class MailBox {

    @Id
    private String id;
    private String ownerId;
    private BoxType boxType;
    private List<String> messageList;

    public MailBox(String ownerId, BoxType boxType) {
        this.ownerId = ownerId;
        this.boxType = boxType;
        this.messageList = new ArrayList<>();
    }
}
