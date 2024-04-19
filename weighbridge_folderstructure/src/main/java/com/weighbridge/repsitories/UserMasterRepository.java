package com.weighbridge.repsitories;

import com.weighbridge.entities.UserMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, String> {

    Page<UserMaster> findAll(Pageable pageable);

    boolean existsByUserEmailIdOrUserContactNo(String emailId, String contactNo);

    boolean existsByUserEmailIdAndUserIdNotOrUserContactNoAndUserIdNot(String emailId, String userId, String contactNo, String userId1);

    @Query("SELECT um FROM UserMaster um JOIN FETCH um.company JOIN FETCH um.site WHERE um.userId = :userId")
    Optional<UserMaster> findByUserIdWithCompanyAndSite(@Param("userId") String userId);


}
