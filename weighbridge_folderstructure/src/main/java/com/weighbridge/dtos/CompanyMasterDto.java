package com.weighbridge.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyMasterDto {
    private String companyId;

    @NotBlank(message = "Company is required")
    private String companyName;
}
