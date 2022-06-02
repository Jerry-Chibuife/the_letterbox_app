package com.thejerryuc.theletterbox.dtos.requests;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreationRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
}
