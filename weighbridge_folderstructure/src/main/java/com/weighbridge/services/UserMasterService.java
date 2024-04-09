package com.weighbridge.services;

import com.weighbridge.payloads.UserRequest;
import com.weighbridge.entities.UserMaster;
import com.weighbridge.payloads.UserResponse;

import java.util.List;

public interface UserMasterService {

    UserMaster createUser(UserRequest userRequest);

    List<UserResponse> getAllUsers();
    UserResponse getSingleUser(String userId);

    String deleteUserById(String userId);

    UserResponse updateUserById(UserRequest userRequest, String userId);
}
