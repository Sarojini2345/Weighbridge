package com.weighbridge.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userId;
    private Set<String> roles;
    private LocalDateTime modifiedDate;
    private String modifiedBy;
    private String site;
    private LocalDateTime createdDate;
    private String createdBy;

}
