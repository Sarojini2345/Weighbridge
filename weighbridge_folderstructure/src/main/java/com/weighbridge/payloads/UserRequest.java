package com.weighbridge.payloads;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserRequest {
    @NotBlank(message = "User id is required")
    @Size(min=5, max = 15, message = "UserId id must be between 5 and 15 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "UserId must be alphanumeric")
    private String userId;

    @NotBlank(message = "Site is required")
    private String site;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Email id is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EmailId does not match the required format")
    private String emailId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character (@#$%^&+=), and must not contain any whitespace.")
    private String password;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid contact number format")
    private String contactNo;

//    @NotNull(message = "Role is required")
    private Set<String> role;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    private String lastName;
}
