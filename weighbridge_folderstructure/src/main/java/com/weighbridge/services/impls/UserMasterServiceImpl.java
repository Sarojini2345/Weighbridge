package com.weighbridge.services.impls;

import com.weighbridge.entities.*;
import com.weighbridge.exceptions.ResourceRetrievalException;
import com.weighbridge.payloads.UserRequest;
import com.weighbridge.exceptions.ResourceCreationException;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.UserResponse;
import com.weighbridge.repsitories.*;
import com.weighbridge.services.UserMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMasterServiceImpl implements UserMasterService {


    private final ModelMapper modelMapper;
    private final UserMasterRepository userMasterRepository;
    private final CompanyMasterRepository companyMasterRepository;
    private final SiteMasterRepository siteMasterRepository;
    private final RoleMasterRepository roleMasterRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;

    @Override
    public UserMaster createUser(UserRequest userRequest) {

        CompanyMaster companyMaster = null;
        SiteMaster siteMaster = null;

        try {
            // Check if the userId already exists in the UserMaster table
            boolean userIdExistsInUserMaster = userMasterRepository.existsByUserId(userRequest.getUserId());
            if (userIdExistsInUserMaster) {
                // If userId exists, throw a BadRequest exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is already taken");
            }

            // Find the company by name in the CompanyMaster table
            companyMaster = companyMasterRepository.findByCompanyName(userRequest.getCompany());
            if (companyMaster == null){
                // If company is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("Company","name", userRequest.getCompany() );
            }

            // Find the site by name in the SiteMaster table
            siteMaster = siteMasterRepository.findBySiteName(userRequest.getSite());
            if (siteMaster == null){
                // If site is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("CompanySite","name", userRequest.getSite());
            }

//            roleMaster = roleMasterRepository.findByRoleName(userRequest.getRole());
//            if(roleMaster == null){
//                throw new ResourceNotFoundException("Role", "roleName", userRequest.getRole());
//            }

            // Check if the userId exists in the UserAuthentication table
            boolean userIdExistsInUserAuthentication = userAuthenticationRepository.existsByUserId(userRequest.getUserId());
            if (userIdExistsInUserAuthentication) {
                // If userId exists, throw a BadRequest exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is already taken");
            }

        } catch (DataAccessException e) {
            // Catch any database access exceptions and throw an InternalServerError exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
        }

        // Create a new UserMaster instance and set its properties from the request
        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(userRequest.getUserId());
        userMaster.setCompany(companyMaster);
        userMaster.setSite(siteMaster);
        userMaster.setUserEmailId(userRequest.getEmailId());
        userMaster.setUserContactNo(userRequest.getContactNo());
        userMaster.setUserFirstName(userRequest.getFirstName());
        userMaster.setUserMiddleName(userRequest.getMiddleName());
        userMaster.setUserLastName(userRequest.getLastName());

        // Create a new UserAuthentication instance and set its properties
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(userRequest.getUserId());
        userAuthentication.setUserPassword(userRequest.getPassword());

        Set<String> setOfRoles = userRequest.getRole();
        Set<RoleMaster> roles = new HashSet<>();

        if (setOfRoles != null) {
            setOfRoles.forEach(roleName -> {
                RoleMaster roleMaster = roleMasterRepository.findByRoleName(roleName);
                if (roleMaster != null) {
                    roles.add(roleMaster);
                } else {
                    // Handle case where role doesn't exist
                    throw new ResourceNotFoundException("Role", "roleName", roleName);
                }
            });
        }
        userAuthentication.setRoles(roles);

        try {
            // Save user to the UserMaster table
            UserMaster savedUser = userMasterRepository.save(userMaster);

            // Save the user authentication details to the UserAuthentication table
            userAuthenticationRepository.save(userAuthentication);

            // Return the saved user object
            return savedUser;
        } catch (DataIntegrityViolationException e){
            if (e.getMessage().contains("email_id")){
                throw new ResourceCreationException("EmailId already exists", e);
            } else if (e.getMessage().contains("contact_no")){
                throw new ResourceCreationException("ContactNo already exists", e);
            } else {
                throw new ResourceCreationException("Failed to create User", e);
            }
        } catch (Exception e) {
            // Catch any exceptions during save operations and throw a ResourceCreationException
            throw new ResourceCreationException("Failed to create User", e);
        }


    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserMaster> allUsers = null;

        try {
            allUsers = userMasterRepository.findAll();
        } catch (Exception e) {
            throw new ResourceRetrievalException("Failed to retrieve users", e);
        }

        List<UserResponse> responses = allUsers.stream()
                .map(userMaster -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setUserId(userMaster.getUserId());
                    userResponse.setFirstName(userMaster.getUserFirstName());
                    userResponse.setMiddleName(userMaster.getUserMiddleName());
                    userResponse.setLastName(userMaster.getUserLastName());
                    userResponse.setEmailId(userMaster.getUserEmailId());
                    userResponse.setContactNo(userMaster.getUserContactNo());

                    CompanyMaster company = userMaster.getCompany();
                    userResponse.setCompany(company.getCompanyName());

                    SiteMaster site = userMaster.getSite();
                    userResponse.setSite(site.getSiteName());

                    Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userMaster.getUserId());
                    // Convert Set<RoleMaster> to Set<String> using Java Streams
                    Set<String> roleNames = roleMasters.stream()
                            .map(RoleMaster::getRoleName) // Assuming getRoleName() returns the role name as String
                            .collect(Collectors.toSet());
                    userResponse.setRole(roleNames);

                    return userResponse;
                })
                .collect(Collectors.toList());

        return responses;
    }

    @Override
    public UserResponse getSingleUser(String userId) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(userMaster.getUserId());
        userResponse.setFirstName(userMaster.getUserFirstName());
        userResponse.setMiddleName(userMaster.getUserMiddleName());
        userResponse.setLastName(userMaster.getUserLastName());
        userResponse.setEmailId(userMaster.getUserEmailId());
        userResponse.setContactNo(userMaster.getUserContactNo());

        CompanyMaster company = userMaster.getCompany();
        userResponse.setCompany(company.getCompanyName());

        SiteMaster site = userMaster.getSite();
        userResponse.setSite(site.getSiteName());

        Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userMaster.getUserId());
        // Convert Set<RoleMaster> to Set<String> using Java Streams
        Set<String> roleNames = roleMasters.stream()
                .map(RoleMaster::getRoleName) // Assuming getRoleName() returns the role name as String
                .collect(Collectors.toSet());
        userResponse.setRole(roleNames);

        return userResponse;
    }

    @Override
    public String deleteUserById(String userId) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if(userMaster.getUserStatus().equals("ACTIVE")){
            userMaster.setUserStatus("INACTIVE");
            userMasterRepository.save(userMaster);
        }
        return "User is InActive";
    }

    @Override
    public UserResponse updateUserById(UserRequest userRequest, String userId) {
        // Check if the user is existing with provided userId
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        CompanyMaster companyMaster = null;
        SiteMaster siteMaster = null;

        try {
            // Find the company by name in the CompanyMaster table
            companyMaster = companyMasterRepository.findByCompanyName(userRequest.getCompany());
            if (companyMaster == null){
                // If company is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("Company","name", userRequest.getCompany() );
            }

            // Find the site by name in the SiteMaster table
            siteMaster = siteMasterRepository.findBySiteName(userRequest.getSite());
            if (siteMaster == null){
                // If site is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("CompanySite","name", userRequest.getSite());
            }

        } catch (DataAccessException e) {
            // Catch any database access exceptions and throw an InternalServerError exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
        }

        // Set userMaster object properties from the request
        userMaster.setCompany(companyMaster);
        userMaster.setSite(siteMaster);
        userMaster.setUserEmailId(userRequest.getEmailId());
        userMaster.setUserContactNo(userRequest.getContactNo());
        userMaster.setUserFirstName(userRequest.getFirstName());
        userMaster.setUserMiddleName(userRequest.getMiddleName());
        userMaster.setUserLastName(userRequest.getLastName());

        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);
        // todo: if password is coming then i have to also set the password
        Set<String> setOfRoles = userRequest.getRole();
        Set<RoleMaster> updatedRoles = new HashSet<>(userAuthentication.getRoles()); // Copy current roles

        if (setOfRoles != null) {
            // Add new roles
            setOfRoles.forEach(roleName -> {
                RoleMaster roleMaster = roleMasterRepository.findByRoleName(roleName);
                if (roleMaster != null && !userAuthentication.getRoles().contains(roleMaster)) {
                    updatedRoles.add(roleMaster);
                } else if (roleMaster == null){
                    throw new ResourceNotFoundException("Role", "roleName", roleName);
                }
            });

            // Remove roles that are not in the request
            updatedRoles.removeIf(role -> !setOfRoles.contains(role.getRoleName()));
        }

        // Set the updated roles to the userAuthentication object
        userAuthentication.setRoles(updatedRoles);

        UserMaster updatedUser = null;
        try {
            // Save user to the UserMaster table
            updatedUser = userMasterRepository.save(userMaster);

            // Save the user authentication details to the UserAuthentication table
            userAuthenticationRepository.save(userAuthentication);

        } catch (Exception e) {
            // Catch any exceptions during save operations and throw a ResourceCreationException
            throw new ResourceCreationException("Failed to Update User", e);
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(updatedUser.getUserId());
        userResponse.setFirstName(updatedUser.getUserFirstName());
        userResponse.setMiddleName(updatedUser.getUserMiddleName());
        userResponse.setLastName(updatedUser.getUserLastName());
        userResponse.setEmailId(updatedUser.getUserEmailId());
        userResponse.setContactNo(updatedUser.getUserContactNo());

        CompanyMaster company = updatedUser.getCompany();
        userResponse.setCompany(company.getCompanyName());

        SiteMaster site = updatedUser.getSite();
        userResponse.setSite(site.getSiteName());

        Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userId);
        // Convert Set<RoleMaster> to Set<String> using Java Streams
        Set<String> roleNames = roleMasters.stream()
                .map(RoleMaster::getRoleName) // Assuming getRoleName() returns the role name as String
                .collect(Collectors.toSet());
        userResponse.setRole(roleNames);

        return userResponse;

    }

}





//    @Override
//    public UserMaster updateUserById(UserRequest userRequest) {
//        // Validate the user request before processing
//        log.info("hi");
//
//        log.info("Hello");
//        log.info(String.valueOf(userRequest));
//
//        UserMaster user = userMasterRepository.findById(userRequest.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userRequest.getUserId()));
//
//        log.info(String.valueOf(user));
//
//        // Update user details
//        user.setUserEmailId(userRequest.getEmailId());
//        CompanyMaster companyMaster = companyMasterRepository.findByCompanyName(userRequest.getCompany());
//        user.setCompany(companyMaster);
//        SiteMaster siteMaster = siteMasterRepository.findByCompanySiteName(userRequest.getCompanySite());
//        user.setCompanySite(siteMaster);
//        user.setUserFirstName(userRequest.getFirstName());
//        user.setUserLastName(userRequest.getLastName());
//
//        // Update password if provided
//        if (userRequest.getPassword() != null) {
//            user.setPassword(userRequest.getPassword());
//        }
//        log.info("hi user");
//        // Update roles
//        Set<String> strRoles = userRequest.getRole();
//        Set<RoleMaster> roles = new HashSet<>();
//        log.info(strRoles.toString());
//        if (strRoles != null) {
//            strRoles.forEach(roleName -> {
//                RoleMaster roleMaster = roleMasterRepository.findByRoleName(roleName);
//                if (roleMaster != null) {
//                    roles.add(roleMaster);
//                } else {
//                    // Handle case where role doesn't exist
//                    throw new ResourceNotFoundException("Role", "roleName", roleName);
//                }
//            });
//        }
//        user.setRoles(roles);
//
//        // Save user
//        return userMasterRepository.save(user);
//    }




//    public void validateUserRequest(UserRequest userRequest){
//        CompanyMaster companyMaster = null;
//        SiteMaster siteMaster = null;
//        RoleMaster roleMaster = null;
//
//        try {
//            boolean userIdExistsInUserMaster = userMasterRepository.existsByUserId(userRequest.getUserId());
//            if (userIdExistsInUserMaster) {
//                log.info("1");
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is already taken");
//
//            }
//            log.info("2");
//            companyMaster = companyMasterRepository.findByCompanyName(userRequest.getCompany());
//            if (companyMaster == null){
//                throw new ResourceNotFoundException("Company","name", userRequest.getCompany() );
//            }
//
//            siteMaster = siteMasterRepository.findByCompanySiteName(userRequest.getCompanySite());
//            if (siteMaster == null){
//                throw new ResourceNotFoundException("CompanySite","name", userRequest.getCompanySite());
//            }
//
////            roleMaster = roleMasterRepository.findByRoleName(userRequest.getRole());
////            if(roleMaster == null){
////                throw new ResourceNotFoundException("Role", "roleName", userRequest.getRole());
////            }
//
//            // Check userId exists in the UserAuthentication table or not
//         //   boolean userIdExistsInUserAuthentication = userAuthenticationRepository.existsByUserId(userRequest.getUserId());
//           /* if (userIdExistsInUserAuthentication) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId is already taken");
//            }*/
//
//        } catch (DataAccessException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
//        }
//    }



//}
