package com.weighbridge.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransporterRequest {
    @NotBlank(message = "transporter name required")
    private String transporterName;


    private String transporterContactNo;

    private String transporterEmailId;

    @NotBlank(message = "transporter Address required")
    private String transporterAddress;
}
