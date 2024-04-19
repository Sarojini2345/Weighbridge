package com.weighbridge.dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierMasterDto {

    private Long supplierId;

    @NotBlank(message = "supplier Name is required")
    private String supplierName;

    private String emailId;
    private String supplierEmail;
    private String supplierContactNo;
    private String supplierAddressLine1;
    private String supplierAddressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
