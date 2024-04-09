package com.weighbridge.repsitories;

import com.weighbridge.entities.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMasterRepository extends JpaRepository<UserMaster, String> {
    boolean existsByUserId(String userId);
}
