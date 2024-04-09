package com.weighbridge.payloads;

import com.weighbridge.entities.SiteMaster;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SiteRequest {
    private String companyName;
    private Set<String> sites;
}
