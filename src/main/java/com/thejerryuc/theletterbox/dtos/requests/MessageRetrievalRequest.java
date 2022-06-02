package com.thejerryuc.theletterbox.dtos.requests;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRetrievalRequest {
    private String mailBoxOwnerId;
    private String mailSender;
    private String mailContent;
    private String mailSubject;
}
