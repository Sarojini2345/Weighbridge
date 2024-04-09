package com.weighbridge.repsitories;

import com.weighbridge.entities.SiteMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteMasterRepository extends JpaRepository<SiteMaster, Integer> {
    @Query("SELECT s.siteName FROM SiteMaster s WHERE s.company.companyId = :companyId")
    List<String> findAllByCompanyId(@Param("companyId") Integer companyId);

    SiteMaster findBySiteName(String site);
}
