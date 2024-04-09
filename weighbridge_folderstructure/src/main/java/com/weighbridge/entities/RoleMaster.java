package com.weighbridge.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_master")
public class RoleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    @NotBlank
    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_status")
    private String roleStatus;

    @Column(name = "role_created_by")
    private String roleCreatedBy;

    @Column(name = "role_created_date")
    private String roleCreatedDate;

    @Column(name = "role_modified_by")
    private String roleModifiedBy;

    @Column(name = "role_modified_date")
    private String roleModifiedDate;

//    @ManyToMany(mappedBy = "roles")
//    private Set<UserAuthentication> users;

}
