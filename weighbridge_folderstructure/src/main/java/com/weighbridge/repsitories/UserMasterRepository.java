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
    boolean existsByUserId(String userId);

    Page<UserMaster> findAllByOrderByUserModifiedByDesc(Pageable pageable);

    boolean existsByUserEmailId(String userEmailId);

    boolean existsByUserContactNo(String contactNo);

}
