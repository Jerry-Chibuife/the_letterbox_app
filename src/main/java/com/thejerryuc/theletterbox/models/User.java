package com.thejerryuc.theletterbox.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String middleName;

    @Getter
    @Setter
    private String lastName;

    @Setter
    @Getter
    private String password;

    @Getter
    @Setter
    private boolean isLoggedIn;

//    @Getter
//    @Setter
//    private String image;

    @Getter
    @Setter
    private List<Notification> notifications;

}
