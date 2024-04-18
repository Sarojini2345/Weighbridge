package com.weighbridge.services.impls;

import com.weighbridge.dtos.VehicleMasterDto;
import com.weighbridge.entities.VehicleMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.repsitories.VehicleMasterRepository;
import com.weighbridge.services.VehicleMasterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class VehicleMasterServiceImpl implements VehicleMasterService {

    @Autowired
    VehicleMasterRepository vehicleMasterRepository;
    @Override
    public String addVehicle(VehicleMasterDto vehicleMasterDto) {

        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(vehicleMasterDto.getVehicleNo()).get();
        if (vehicleMaster!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "vehicle already exist!");
        }
        else{
          //  ModelMapper.map(vehicleMasterDto,VehicleMaster.class);
        }
        return null;
    }
}