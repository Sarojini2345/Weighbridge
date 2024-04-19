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
        if (bySiteNameAndSiteAddress != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "give proper address of the site");
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
            siteId = siteAbbreviation + "01";
            ;
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
        // Find the company by name
        CompanyMaster company = companyMasterRepository.findByCompanyName(siteRequest.getCompanyName());
        if (company == null) {
            throw new ResourceNotFoundException("Company", "companyName", siteRequest.getCompanyName());
        }

        String siteName = siteRequest.getSiteName().trim();
        String siteAddress = siteRequest.getSiteAddress().trim();
        try {
            if (siteName != null && siteAddress != null) {
                // Check if a site with the same name and address exists
                SiteMaster existingSite = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);
                if (existingSite != null) {
                    // Associate the company with the existing site(s)
                    if(!existingSite.getCompany().getCompanyId().equals(company.getCompanyId())) {
                        existingSite.setCompany(company);
                        siteMasterRepository.save(existingSite);
                        return "Site(s) assigned to company successfully";
                    }
                    else {
                        return "company already assigned to this site";
                    }

                } else {
                    // Create a new site if it doesn't exist
                    SiteMaster newSite = new SiteMaster();
                    HttpSession session = httpServletRequest.getSession();
                    String userId = String.valueOf(session.getAttribute("userId"));
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    newSite.setSiteId(generateSiteId(siteRequest.getSiteName()));
                    newSite.setSiteName(siteName);
                    newSite.setSiteAddress(siteAddress);
                    newSite.setCompany(company);
                    newSite.setSiteCreatedBy(userId);
                    newSite.setSiteCreatedDate(currentDateTime);
                    newSite.setSiteModifiedBy(userId);
                    newSite.setSiteModifiedDate(currentDateTime);
                    siteMasterRepository.save(newSite);
                    return "Site(s) assigned to company successfully";
                }

            }
            else{
                return "Site and company are null";
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid site info format
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            // Handle other exceptions
            return "Error assigning site: " + e.getMessage();
        }
    }



    @Override
    public List<Map<String, String>> findAllByCompanySites(String companyName) {
        CompanyMaster companyMaster;

        companyMaster = companyMasterRepository.findByCompanyName(companyName);
        if(companyMaster==null){
            throw new ResourceNotFoundException("company does not exist");
        }
        List<Map<String, String>> allByCompanyId = siteMasterRepository.findAllByCompanyId(companyMaster.getCompanyId());
        return allByCompanyId;
    }
}
// todo: do validation if a site is assigned to company , it show a popup to override it