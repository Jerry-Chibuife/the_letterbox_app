package com.thejerryuc.theletterbox.dtos.responses;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentMessageResponse {
    private boolean isSuccessful;
    private String response;
}
