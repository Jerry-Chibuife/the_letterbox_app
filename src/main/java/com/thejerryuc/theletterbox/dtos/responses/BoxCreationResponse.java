package com.thejerryuc.theletterbox.dtos.responses;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxCreationResponse {
    private boolean isSuccessful;
    private String message;
}
