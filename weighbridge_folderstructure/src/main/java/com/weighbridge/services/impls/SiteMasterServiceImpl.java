package com.weighbridge.services.impls;

import com.weighbridge.dtos.SiteMasterDto;
import com.weighbridge.entities.CompanyMaster;
import com.weighbridge.entities.SiteMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.SiteRequest;
import com.weighbridge.repsitories.CompanyMasterRepository;
import com.weighbridge.repsitories.SiteMasterRepository;
import com.weighbridge.services.SiteMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteMasterServiceImpl implements SiteMasterService {

    private final SiteMasterRepository siteMasterRepository;
    private final ModelMapper modelMapper;
    private final CompanyMasterRepository companyMasterRepository;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Override
    public SiteMasterDto createSite(SiteMasterDto siteMasterDto) {
        SiteMaster bySiteNameAndSiteAddress = siteMasterRepository.findBySiteNameAndSiteAddress(siteMasterDto.getSiteName(), siteMasterDto.getSiteAddress());
        if(bySiteNameAndSiteAddress!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"give proper address of the site");
        }
        siteMasterDto.setSiteId(generateSiteId(siteMasterDto.getSiteName()));

        SiteMaster site = modelMapper.map(siteMasterDto, SiteMaster.class);
        HttpSession session = httpServletRequest.getSession();
        String userId = String.valueOf(session.getAttribute("userId"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        site.setSiteCreatedBy(userId);
        site.setSiteCreatedDate(currentDateTime);
        site.setSiteModifiedBy(userId);
        site.setSiteModifiedDate(currentDateTime);
        SiteMaster savedSite = siteMasterRepository.save(site);

        return modelMapper.map(savedSite, SiteMasterDto.class);
    }

    private String generateSiteId(String siteName) {
        // Extract the first three letters of the site name (or abbreviation)
        String siteAbbreviation = siteName.substring(0, Math.min(siteName.length(), 3)).toUpperCase();

        // Retrieve the count of existing site names that start with the same abbreviation
        long siteCount = siteMasterRepository.countBySiteNameStartingWith(siteAbbreviation);

        // Generate the site ID based on the count
        String siteId;
        if (siteCount > 0) {
            // If other sites with the same abbreviation exist, append a numeric suffix
            siteId = String.format("%s%02d", siteAbbreviation, siteCount + 1);
        } else {
            // Otherwise, use the abbreviation without a suffix
            siteId = siteAbbreviation+ "01";;
        }

        return siteId;
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
        if(listOfSites != null) {
            for (String siteInfo : listOfSites) {
                // Split the siteInfo to separate site name and site address
                String[] siteInfoParts = siteInfo.split(",", 2);
                if (siteInfoParts.length != 2) {
                    throw new IllegalArgumentException("Invalid format for site info: " + siteInfo);
                }
                String siteName = siteInfoParts[0].trim();
                String siteAddress = siteInfoParts[1].trim();

                // Check if a site with the same name and address exists
                SiteMaster existingSites = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);
                if (existingSites!=null) {
                    // Associate the company with the existing site(s)
                   existingSites.setCompany(company);
                    siteMasterRepository.save(existingSites);
                }
            }
        }
        return "Site Assigned to company successful";
    }

    @Override
    public List<Map<String, String>> findAllByCompanySites(String companyName) {
        CompanyMaster company=companyMasterRepository.findByCompanyName(companyName);
        List<Map<String, String>> allByCompanyId = siteMasterRepository.findAllByCompanyId(company.getCompanyId());
        return allByCompanyId;
    }
}
// todo: do validation if a site is assigned to company , it show a popup to override it