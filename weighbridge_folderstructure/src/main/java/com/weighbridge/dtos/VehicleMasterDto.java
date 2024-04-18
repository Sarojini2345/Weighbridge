package com.weighbridge.dtos;

import com.weighbridge.entities.TransporterMaster;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VehicleMasterDto {
    private String vehicleNo;

    private String vehicleType;

    private String vehicleManufacturer;

    private Integer wheelsNo;

    private Double tareWeight;

    private Double loadCapacity;

    private Date fitnessUpto;

    private List<TransporterMaster> transporterMasters;
}