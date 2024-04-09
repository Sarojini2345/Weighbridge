package com.weighbridge.controllers;

import com.weighbridge.payloads.UserRequest;
import com.weighbridge.entities.UserMaster;
import com.weighbridge.payloads.UserResponse;
import com.weighbridge.services.UserMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserMasterController {

    private final UserMasterService userMasterService;

    // Create new user
    @PostMapping
    public ResponseEntity<UserMaster> createUser(@Validated @RequestBody UserRequest userRequest){
        UserMaster savedUser = userMasterService.createUser(userRequest);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> userLists = userMasterService.getAllUsers();
        return ResponseEntity.ok(userLists);
    }

//     Get single user by userId
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getSingleUser(@PathVariable("userId") String userId){
        UserResponse user = userMasterService.getSingleUser(userId);
        return ResponseEntity.ok(user);
    }

    // Delete user by userId
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable String userId){
        String response = userMasterService.deleteUserById(userId);
        return ResponseEntity.ok(response);
    }

    // Update user by userId
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<UserResponse> updateUserById(@Validated @RequestBody UserRequest userRequest, @PathVariable String userId){

        UserResponse response = userMasterService.updateUserById(userRequest, userId);
        return ResponseEntity.ok(response);
    }

}
