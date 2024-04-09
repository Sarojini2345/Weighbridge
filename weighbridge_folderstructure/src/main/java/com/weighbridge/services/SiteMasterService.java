package com.weighbridge.services;

import com.weighbridge.dtos.SiteMasterDto;
import com.weighbridge.payloads.SiteRequest;

import java.util.List;

public interface SiteMasterService {
    SiteMasterDto createSite(SiteMasterDto siteMasterDto);

    List<SiteMasterDto> getAllSite();

    String assignSite(SiteRequest siteRequest);

    List<String> findAllByCompanySites(String companyName);
}
