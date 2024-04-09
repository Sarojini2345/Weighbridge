package com.weighbridge.services.impls;

import com.weighbridge.dtos.SiteMasterDto;
import com.weighbridge.entities.CompanyMaster;
import com.weighbridge.entities.RoleMaster;
import com.weighbridge.entities.SiteMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.SiteRequest;
import com.weighbridge.repsitories.CompanyMasterRepository;
import com.weighbridge.repsitories.SiteMasterRepository;
import com.weighbridge.services.SiteMasterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteMasterServiceImpl implements SiteMasterService {

    private final SiteMasterRepository siteMasterRepository;
    private final ModelMapper modelMapper;
    private final CompanyMasterRepository companyMasterRepository;
    @Override
    public SiteMasterDto createSite(SiteMasterDto siteMasterDto) {
        SiteMaster site = modelMapper.map(siteMasterDto, SiteMaster.class);

        SiteMaster savedSite = siteMasterRepository.save(site);

        return modelMapper.map(savedSite, SiteMasterDto.class);
    }

    @Override
    public List<SiteMasterDto> getAllSite() {
        List<SiteMaster> sites = siteMasterRepository.findAll();
        return sites.stream().map(site -> modelMapper.map(site, SiteMasterDto.class)).collect(Collectors.toList());
    }

    @Override
    public String assignSite(SiteRequest siteRequest) {

        CompanyMaster company = companyMasterRepository.findByCompanyName(siteRequest.getCompanyName());
        if(company == null){
            throw new ResourceNotFoundException("Company", "companyName", siteRequest.getCompanyName());
        }

        Set<String> listOfSites = siteRequest.getSites();
        Set<SiteMaster> sites = new HashSet<>();
        if (listOfSites != null) {
            listOfSites.forEach(siteName -> {
                SiteMaster site = siteMasterRepository.findBySiteName(siteName);
                if (site != null) {
                    site.setCompany(company);
                    siteMasterRepository.save(site);
                } else {
                    // Handle case where role doesn't exist
                    throw new ResourceNotFoundException("Site", "siteName", siteName);
                }
            });
        }
        return "Site Assigned to company successful";
    }

    @Override
    public List<String> findAllByCompanySites(String companyName) {
        CompanyMaster company=companyMasterRepository.findByCompanyName(companyName);
        List<String> allByCompanyId = siteMasterRepository.findAllByCompanyId(company.getCompanyId());
        return allByCompanyId;
    }
}

// todo: do validation if a site is assigned to company , it show a popup to override it