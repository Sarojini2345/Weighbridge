package com.weighbridge.services.impls;

import com.weighbridge.dtos.LoginDto;
import com.weighbridge.entities.UserAuthentication;
import com.weighbridge.entities.UserMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.repsitories.UserAuthenticationRepository;
import com.weighbridge.repsitories.UserMasterRepository;
import com.weighbridge.services.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService{
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Override
    public String loginUser(LoginDto dto) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(dto.getUserId());
        if(userAuthentication==null){
            throw new ResourceNotFoundException("User", "userId",dto.getUserId());
        }
        UserMaster userMaster=userMasterRepository.findById(dto.getUserId()).orElseThrow(()->new ResourceNotFoundException("User","userId", dto.getUserId()));
        if(userMaster.getUserStatus().equals("INACTIVE")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User is inactive");
        }
        String password=userAuthenticationRepository.findPasswordByUserId(dto.getUserId());
        if(password.equals(dto.getUserPassword())){
             return "User logged in successfully !";
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid userId or password");
        }
    }
}
