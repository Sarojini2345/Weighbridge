package com.weighbridge.repsitories;

import com.weighbridge.entities.RoleMaster;
import com.weighbridge.entities.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {

    boolean existsByUserId(String userId);

    @Query("SELECT ua.roles FROM UserAuthentication ua WHERE ua.userId = :userId")
    Set<RoleMaster> findRolesByUserId(String userId);

    UserAuthentication findByUserId(String userId);
@Query("select u.password from UserAuthentication u where u.userId=:userId")
    String findPasswordByUserId(String userId);
}
