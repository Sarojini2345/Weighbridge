package com.weighbridge.services.impls;

import com.weighbridge.dtos.LoginDto;
import com.weighbridge.dtos.ResetPasswordDto;
import com.weighbridge.entities.*;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.LoginResponse;
import com.weighbridge.repsitories.SiteMasterRepository;
import com.weighbridge.repsitories.UserAuthenticationRepository;
import com.weighbridge.repsitories.UserMasterRepository;
import com.weighbridge.services.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;

    @Value("${app.default-password}")
    private String defaultPassword;

    @Autowired
    private SiteMasterRepository siteMasterRepository;
    @Autowired
    HttpServletRequest request;

    @Override
    public LoginResponse loginUser(LoginDto dto) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(dto.getUserId());
        if (userAuthentication == null) {
            throw new ResourceNotFoundException("User", "userId", dto.getUserId());
        }
        UserMaster userMaster = userMasterRepository.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "userId", dto.getUserId()));

        if (userMaster.getUserStatus().equals("INACTIVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is inactive");
        }
        if(!userAuthentication.getUserPassword().equals(dto.getUserPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId or password");
        }
        if(dto.getUserPassword().equals(defaultPassword)){
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessage("please reset your password.");
            loginResponse.setUserId(dto.getUserId());
            return loginResponse;
        }
        String password = userAuthenticationRepository.findPasswordByUserId(dto.getUserId());
        if (password.equals(dto.getUserPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", dto.getUserId());

            //for getting the site details from user master
            SiteMaster siteMaster = userMaster.getSite();
            //add userSite to session so that after login , fetch from session
            session.setAttribute("userSite", siteMaster.getSiteId());

            //for getting the company details from user master
            CompanyMaster companyMaster = userMaster.getCompany();
            //add userSite to session so that after login , fetch from session
            session.setAttribute("userCompany", companyMaster.getCompanyId());


            LoginResponse loginResponse = new LoginResponse();
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
            session.setAttribute("roles", roles);

            if(userMaster.getUserMiddleName()!=null){
                loginResponse.setUserName(userMaster.getUserFirstName()+" "+userMaster.getUserMiddleName()+" "+userMaster.getUserLastName());
            }
            else{
                loginResponse.setUserName(userMaster.getUserFirstName()+" "+userMaster.getUserLastName());
            }
            return loginResponse;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId or password");
        }
    }

    @Override
    public UserAuthentication resetPassword(String userId, ResetPasswordDto resetPasswordDto) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);
        System.out.println("password: "+resetPasswordDto.getPassword());
        userAuthentication.setUserPassword(resetPasswordDto.getPassword());
        UserAuthentication saveUser = userAuthenticationRepository.save(userAuthentication);
        return saveUser;
    }
}
