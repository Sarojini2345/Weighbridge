package com.weighbridge.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleMasterDto {
    private Integer roleId;

    @NotBlank(message = "Role is required")
    private String roleName;
}

