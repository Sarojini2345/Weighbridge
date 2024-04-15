package com.weighbridge.repsitories;

import com.weighbridge.entities.UserMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, String> {

    Page<UserMaster> findAllByOrderByUserModifiedByDesc(Pageable pageable);

    boolean existsByUserEmailIdOrUserContactNo(String emailId, String contactNo);

    boolean existsByUserEmailIdAndUserIdNotOrUserContactNoAndUserIdNot(String emailId, String userId, String contactNo, String userId1);
}
