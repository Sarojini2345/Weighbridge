package com.weighbridge.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "site_master")
public class SiteMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id")
    private int siteId;

    @NotBlank
    @Column(name = "site_name")
    private String siteName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    private CompanyMaster company;

    @Column(name = "site_address")
    private String siteAddress;

    @Column(name = "site_status")
    private String siteStatus="ACTIVE";

    @Column(name = "site_created_by")
    private String siteCreatedBy;

    @Column(name = "site_created_date")
    private String siteCreatedDate;

    @Column(name = "site_modified_by")
    private String siteModifiedBy;

    @Column(name = "site_modified_date")
    private String siteModifiedDate;

}
