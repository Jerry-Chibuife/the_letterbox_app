package com.thejerryuc.theletterbox.models;


import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    private String id;
    private String title;
    private String messageId;
    private LocalDateTime timeStamp;

    public Notification(String title, String messageId, LocalDateTime timeStamp) {
        this.title = title;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
    }
}
