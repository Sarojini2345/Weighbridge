package com.weighbridge.services;

import com.weighbridge.dtos.LoginDto;
import com.weighbridge.entities.UserAuthentication;

public interface UserAuthenticationService {

    String loginUser(LoginDto dto);
}
