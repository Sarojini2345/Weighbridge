package com.weighbridge.services.impls;

import com.weighbridge.entities.*;
import com.weighbridge.exceptions.ResourceRetrievalException;
import com.weighbridge.payloads.UpdateRequest;
import com.weighbridge.payloads.UserRequest;
import com.weighbridge.exceptions.ResourceCreationException;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.UserResponse;
import com.weighbridge.repsitories.*;
import com.weighbridge.services.UserMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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


    private final SequenceGeneratorRepository sequenceGeneratorRepository;



    @Autowired
    HttpServletRequest request;


    // TODO put validation for company and site, if site does not belong to company than it shouldn't create
    @Override
    public String createUser(UserRequest userRequest) {

        CompanyMaster companyMaster = null;
        SiteMaster siteMaster = null;

        try {
            // Check if the userId already exists in the UserMaster table
            boolean userEmailIdExistsInUserMaster = userMasterRepository.existsByUserEmailId(userRequest.getEmailId());
            if (userEmailIdExistsInUserMaster) {
                // If userId exists, throw a BadRequest exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Id is already taken");
            }
            boolean userContactExistsInUserMaster = userMasterRepository.existsByUserContactNo(userRequest.getContactNo());
            if (userContactExistsInUserMaster) {
                // If userId exists, throw a BadRequest exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact No is already taken");
            }

            // Find the company by name in the CompanyMaster table
            companyMaster = companyMasterRepository.findByCompanyName(userRequest.getCompany());
            if (companyMaster == null) {
                // If company is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("Company", "name", userRequest.getCompany());
            }

            // Find the site by name in the SiteMaster table
            siteMaster = siteMasterRepository.findBySiteName(userRequest.getSite());
            if (siteMaster == null) {
                // If site is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("CompanySite", "name", userRequest.getSite());
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
        String str = generateUserId(companyMaster.getCompanyId(), siteMaster.getSiteId());
        userMaster.setUserId(str);
        userMaster.setCompany(companyMaster);
        userMaster.setSite(siteMaster);
        userMaster.setUserEmailId(userRequest.getEmailId());
        userMaster.setUserContactNo(userRequest.getContactNo());
        userMaster.setUserFirstName(userRequest.getFirstName());
        userMaster.setUserMiddleName(userRequest.getMiddleName());
        userMaster.setUserLastName(userRequest.getLastName());
        HttpSession session = request.getSession();
        userMaster.setUserCreatedBy(String.valueOf(session.getAttribute("userId")));
        userMaster.setUserCreatedDate(LocalDateTime.now());
        userMaster.setUserModifiedBy(String.valueOf(session.getAttribute("userId")));
        userMaster.setUserModifiedDate(LocalDateTime.now());

        // Create a new UserAuthentication instance and set its properties
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(str);
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
        log.info(String.valueOf(userMaster));


        try {
            // Save user to the UserMaster table
            UserMaster savedUser = userMasterRepository.save(userMaster);

            // Save the user authentication details to the UserAuthentication table
            userAuthenticationRepository.save(userAuthentication);

            // Return the saved user object
            return "User is created successfully with userid : " + savedUser.getUserId();
        } catch (Exception e) {
            // Catch any exceptions during save operations and throw a ResourceCreationException
            throw new ResourceCreationException("Failed to create User", e);
        }


    }
    public synchronized String generateUserId(String companyId, String siteId) {
        // Retrieve the current value of the unique identifier from the database for the given company and site
        SequenceGenerator sequenceGenerator = sequenceGeneratorRepository.findByCompanyIdAndSiteId(companyId, siteId)
                .orElse(new SequenceGenerator(companyId, siteId, 1)); // Initialize to 1 if not found
        int uniqueIdentifier = sequenceGenerator.getNextValue();

        // Concatenate company ID, site ID, and unique identifier
        String userId = companyId + "_" + siteId + "_" + String.format("%02d", uniqueIdentifier);

        // Increment the unique identifier
        uniqueIdentifier = (uniqueIdentifier + 1) % 1000; // Ensure it's always 3 digits

        // Update the unique identifier value in the database
        sequenceGenerator.setNextValue(uniqueIdentifier);
        sequenceGeneratorRepository.save(sequenceGenerator);

        return userId;
    }


//    public static synchronized String generateUserId(String companyId, String siteId) {
//        // Concatenate company ID, site ID, and unique identifier
//
//        String userId = companyId + "_" + siteId + "_" + String.format("%02d", uniqueIdentifier);
//
//        // Increment the unique identifier
//        uniqueIdentifier = (uniqueIdentifier + 1) % 1000; // Ensure it's always 3 digits
//
//        return userId;
//    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserMaster> userPage = userMasterRepository.findAllByOrderByUserModifiedByDesc(pageable);

        Page<UserResponse> responsePage = userPage.map(userMaster -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(userMaster.getUserId());
            userResponse.setFirstName(userMaster.getUserFirstName());
            userResponse.setMiddleName(userMaster.getUserMiddleName());
            userResponse.setLastName(userMaster.getUserLastName());
            userResponse.setEmailId(userMaster.getUserEmailId());
            userResponse.setContactNo(userMaster.getUserContactNo());

            CompanyMaster company = userMaster.getCompany();
            userResponse.setCompany(company != null ? company.getCompanyName() : null);

            SiteMaster site = userMaster.getSite();
            userResponse.setSite(site != null ? site.getSiteName() : null);

            Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userMaster.getUserId());
            Set<String> roleNames = roleMasters.stream()
                    .map(RoleMaster::getRoleName)
                    .collect(Collectors.toSet());
            userResponse.setRole(roleNames);
            userResponse.setStatus(userMaster.getUserStatus());

            return userResponse;
        });

        return responsePage;
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
        userResponse.setStatus(userMaster.getUserStatus());

        return userResponse;
    }

    @Override
    public String deleteUserById(String userId) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (userMaster.getUserStatus().equals("ACTIVE")) {
            userMaster.setUserStatus("INACTIVE");
            userMasterRepository.save(userMaster);
        }
        return "User is InActive";
    }

    @Override
    public UserResponse updateUserById(UpdateRequest updateRequest, String userId) {
        // Check if the user is existing with provided userId
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        CompanyMaster companyMaster = null;
        SiteMaster siteMaster = null;

        try {
            // Find the company by name in the CompanyMaster table
            companyMaster = companyMasterRepository.findByCompanyName(updateRequest.getCompany());
            if (companyMaster == null) {
                // If company is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("Company", "name", updateRequest.getCompany());
            }

            // Find the site by name in the SiteMaster table
            siteMaster = siteMasterRepository.findBySiteName(updateRequest.getSite());
            if (siteMaster == null) {
                // If site is not found, throw a ResourceNotFoundException
                throw new ResourceNotFoundException("CompanySite", "name", updateRequest.getSite());
            }

            if (userMasterRepository.existsByUserContactNoAndUserIdNot(updateRequest.getContactNo(), userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Contact number is already in use by another user");
            }
            if (userMasterRepository.existsByUserEmailIdAndUserIdNot(updateRequest.getEmailId(), userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"EmailId is already in use by another user");
            }

        } catch (DataAccessException e) {
            // Catch any database access exceptions and throw an InternalServerError exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
        }

        // Set userMaster object properties from the request
        userMaster.setCompany(companyMaster);
        userMaster.setSite(siteMaster);
        userMaster.setUserEmailId(updateRequest.getEmailId());
        userMaster.setUserContactNo(updateRequest.getContactNo());
        userMaster.setUserFirstName(updateRequest.getFirstName());
        userMaster.setUserMiddleName(updateRequest.getMiddleName());
        userMaster.setUserLastName(updateRequest.getLastName());

        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);
        // todo: if password is coming then i have to also set the password
        Set<String> setOfRoles = updateRequest.getRole();
        Set<RoleMaster> updatedRoles = new HashSet<>(userAuthentication.getRoles()); // Copy current roles

        if (setOfRoles != null) {
            // Add new roles
            setOfRoles.forEach(roleName -> {
                RoleMaster roleMaster = roleMasterRepository.findByRoleName(roleName);
                if (roleMaster != null && !userAuthentication.getRoles().contains(roleMaster)) {
                    updatedRoles.add(roleMaster);
                } else if (roleMaster == null) {
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


