package com.thejerryuc.theletterbox.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationResponse {
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
}
