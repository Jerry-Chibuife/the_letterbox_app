package com.thejerryuc.theletterbox.controllers;


import com.thejerryuc.theletterbox.dtos.requests.AccountCreationRequest;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/theletterbox")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/createUser")
    public ResponseEntity<?> registerUser(@RequestBody AccountCreationRequest request){
        try {
            return new ResponseEntity<>(userService.registerUser(request), HttpStatus.CREATED);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping
//    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password){
//        try {
//            return new ResponseEntity<>(userService.logUserIn(email, password), HttpStatus.CREATED);
//        }
//        catch (TheLetterBoxAppException exception){
//            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
}
