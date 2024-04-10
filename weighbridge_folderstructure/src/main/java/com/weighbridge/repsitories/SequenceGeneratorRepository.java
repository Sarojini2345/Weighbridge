package com.weighbridge.repsitories;

import com.weighbridge.entities.SequenceGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SequenceGeneratorRepository extends JpaRepository<SequenceGenerator,Long> {


    Optional<SequenceGenerator> findByCompanyIdAndSiteId(String companyId, String siteId);
}
