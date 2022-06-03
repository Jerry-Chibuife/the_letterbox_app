package com.thejerryuc.theletterbox.controllers;


import com.thejerryuc.theletterbox.dtos.requests.AccountCreationRequest;
import com.thejerryuc.theletterbox.exceptions.TheLetterBoxAppException;
import com.thejerryuc.theletterbox.models.Message;
import com.thejerryuc.theletterbox.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password){
        try {
            userService.logUserIn(email, password);
            return new ResponseEntity<>(userService.retrieveUserMailBoxes(email), HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{userEmail}/inbox")
    public ResponseEntity<?> getInbox(@PathVariable("userEmail") String userEmail){
        try {
            return new ResponseEntity<>(userService.retrieveUserInboxFromMailBox(userEmail), HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{userEmail}/sentBox")
    public ResponseEntity<?> getSentBox(@PathVariable("userEmail") String userEmail){
        try {
            return new ResponseEntity<>(userService.retrieveUserSentBoxFromMailBox(userEmail), HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/user/inbox/message")
    public ResponseEntity<?> getMessageFromInbox(@RequestParam String messageId){
        try {
            return new ResponseEntity<>(userService.retrieveMessageFromUserInbox(messageId), HttpStatus.FOUND);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/user/sentBox/message")
    public ResponseEntity<?> getMessageFromSentBox(@RequestParam String messageId){
        try {
            return new ResponseEntity<>(userService.retrieveMessageFromUserSentBox(messageId), HttpStatus.FOUND);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/user/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody Message message){
        try {
            userService.sendMessageToUser(message);
            return new ResponseEntity<>("Message sent successfully", HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/user/sentBox/message")
    public ResponseEntity<?> deleteMessageFromSentBox(@RequestParam String messageId){
        try {
            return new ResponseEntity<>(userService.deleteMessageFromSentBox(messageId), HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/user/inbox/message")
    public ResponseEntity<?> deleteMessageFromInBox(@RequestParam String messageId){
        try {
            return new ResponseEntity<>(userService.deleteMessageFromInbox(messageId), HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{Email}/notification")
    public ResponseEntity<?> deleteNotification(@RequestParam String notificationId, @PathVariable String Email){
        try {
            userService.deleteNotificationFromUserList(Email, notificationId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/{userEmail}/forwardMessage")
    public ResponseEntity<?> forwardMessage(@RequestParam List<String> receiverEmails, @RequestParam String messageId, @PathVariable String userEmail){
        try {
            userService.forwardMessageToAnotherReceiver(userEmail, messageId, receiverEmails);
            return new ResponseEntity<>("Message sent successfully", HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users/{userEmail}/logout")
    public ResponseEntity<?> logout(@RequestParam String confirmation, @PathVariable String userEmail){
        try {
            userService.logUserOut(userEmail, confirmation);
            return new ResponseEntity<>("Message sent successfully", HttpStatus.OK);
        }
        catch (TheLetterBoxAppException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
