package com.weighbridge.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weighbridge.payloads.UpdateRequest;
import com.weighbridge.payloads.UserRequest;
import com.weighbridge.entities.UserMaster;
import com.weighbridge.payloads.UserResponse;
import com.weighbridge.services.UserMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserRequest userRequest){
        String response = userMasterService.createUser(userRequest);
        // Create a JSON object containing the response message
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.createObjectNode().put("message", response).toString();
        return new ResponseEntity<>(jsonResponse, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        Pageable pageable;

        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortField);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<UserResponse> userPage = userMasterService.getAllUsers(pageable);

        List<UserResponse> userLists = userPage.getContent();
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
    public ResponseEntity<UserResponse> updateUserById(@Validated @RequestBody UpdateRequest updateRequest, @PathVariable String userId){

        UserResponse response = userMasterService.updateUserById(updateRequest, userId);
        return ResponseEntity.ok(response);
    }

}
