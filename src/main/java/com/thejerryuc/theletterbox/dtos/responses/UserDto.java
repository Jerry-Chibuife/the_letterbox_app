package com.thejerryuc.theletterbox.dtos.responses;


import com.thejerryuc.theletterbox.models.Notification;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean isLoggedIn;
    private List<Notification> notifications;
//    private String image;
}
