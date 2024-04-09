package com.weighbridge.services.impls;

import com.weighbridge.dtos.LoginDto;
import com.weighbridge.entities.RoleMaster;
import com.weighbridge.entities.UserAuthentication;
import com.weighbridge.entities.UserMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.LoginResponse;
import com.weighbridge.repsitories.UserAuthenticationRepository;
import com.weighbridge.repsitories.UserMasterRepository;
import com.weighbridge.services.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService{
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    HttpServletRequest request;
    @Override
    public LoginResponse loginUser(LoginDto dto) {
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
            HttpSession session=request.getSession();
            session.setAttribute("userId",dto.getUserId());
            LoginResponse loginResponse=new LoginResponse();
            loginResponse.setMessage("User logged in successfully !");


            Set<RoleMaster> setOfRoles = userAuthentication.getRoles();
            Set<String> roles = new HashSet<>();

            if (setOfRoles != null) {
                setOfRoles.forEach(roleName -> {
                    String role = roleName.getRoleName();
                    roles.add(role);
                });
            }
            loginResponse.setRoles(roles);
            session.setAttribute("roles",roles);
            return loginResponse;
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid userId or password");
        }
    }
}
