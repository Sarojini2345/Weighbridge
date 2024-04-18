package com.weighbridge.services;

import com.weighbridge.dtos.VehicleMasterDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface VehicleMasterService{

    public String addVehicle(VehicleMasterDto vehicleMasterDto);
}
