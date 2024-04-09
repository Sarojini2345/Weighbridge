package com.weighbridge.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_authentication")
public class UserAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long userAuthId;   // for indexing
    @Column(name = "user_id", unique = true)
    @NotBlank(message = "UserId is required")
    @Size(min=5, max = 15, message = "UserId id must be between 5 and 15 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "UserId must be alphanumeric")
    private String userId;
    @NotBlank(message = "Password is required!!")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password")
    private String userPassword;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private Set<RoleMaster> roles;
}
