package com.weighbridge.services.impls;

import com.weighbridge.dtos.VehicleMasterDto;
import com.weighbridge.entities.TransporterMaster;
import com.weighbridge.entities.VehicleMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.payloads.UserResponse;
import com.weighbridge.payloads.VehicleResponse;
import com.weighbridge.repsitories.TransporterMasterRepository;
import com.weighbridge.repsitories.VehicleMasterRepository;
import com.weighbridge.services.VehicleMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleMasterServiceImpl implements VehicleMasterService {

    @Autowired
    VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    TransporterMasterRepository transporterMasterRepository;

    @Autowired
    ModelMapper mapper;

    @Autowired
    HttpServletRequest request;

    @Override
    public String addVehicle(VehicleMasterDto vehicleMasterDto) {
        HttpSession session = request.getSession();
        LocalDateTime currentDateTime = LocalDateTime.now();
        VehicleMaster vehicleMasters = new VehicleMaster();
        vehicleMasters.setCreatedDate(currentDateTime);
        vehicleMasters.setModifiedDate(currentDateTime);
        vehicleMasters.setCreatedBy(session.getAttribute("userId").toString());
        vehicleMasters.setModifiedBy(session.getAttribute("userId").toString());
        vehicleMasters.setVehicleNo(vehicleMasterDto.getVehicleNo());
        //assigning transporter to vehicle
        List<TransporterMaster> transporterMasterList = new ArrayList<>();

        transporterMasterList.addAll(getAllTransportMaster(vehicleMasterDto.getVehicleNo()));
      //  System.out.println("--------"+getAllTransportMaster(vehicleMasterDto.getVehicleNo()));
        TransporterMaster byTransporterName = transporterMasterRepository.findByTransporterName(vehicleMasterDto.getTransporterMaster());
        transporterMasterList.add(byTransporterName);

//        transporterMasterList.add(transporterMasterRepository.findByTransporterName("Maa tarini transporter"));
        vehicleMasters.setTransporter(transporterMasterList);

        vehicleMasters.setVehicleManufacturer(vehicleMasterDto.getVehicleManufacturer());
        vehicleMasters.setVehicleType(vehicleMasterDto.getVehicleType());
        vehicleMasters.setLoadCapacity(vehicleMasterDto.getLoadCapacity());
        vehicleMasters.setFitnessUpto(vehicleMasterDto.getFitnessUpto());
        vehicleMasters.setWheelsNo(vehicleMasterDto.getWheelsNo());
        vehicleMasters.setTareWeight(vehicleMasterDto.getTareWeight());
        vehicleMasterRepository.save(vehicleMasters);
        return "Vehicle added successfully";
    }

private List<TransporterMaster> getAllTransportMaster(String vehicleNo){
        return vehicleMasterRepository.findTransportersByVehicleId(vehicleNo);
}
    @Override
    public Page<VehicleResponse> vehicles(Pageable pageable) {
        Page<VehicleMaster> responsePage = vehicleMasterRepository.findAll(pageable);
        Page<VehicleResponse> vehicleResponse = responsePage.map(vehicleMaster -> {
            VehicleResponse vehicleResponses = new VehicleResponse();
            vehicleResponses.setVehicleNo(vehicleMaster.getVehicleNo());
            vehicleResponses.setVehicleType(vehicleMaster.getVehicleType());
            vehicleResponses.setVehicleManufacturer(vehicleMaster.getVehicleManufacturer());
            vehicleResponses.setTransporter(String.valueOf(vehicleMaster.getTransporter()));
            vehicleResponses.setFitnessUpto(vehicleMaster.getFitnessUpto());
            return vehicleResponses;
        });

        return vehicleResponse;
    }

    @Override
    public VehicleResponse vehicleByNo(String vehicleNo) {
        VehicleMaster byId = vehicleMasterRepository.findById(vehicleNo).orElseThrow(() -> new ResourceNotFoundException("VehicleMaster", "Vehicle", vehicleNo));
        VehicleResponse vehicleResponse = new VehicleResponse();
        vehicleResponse.setVehicleNo(vehicleNo);
        vehicleResponse.setVehicleManufacturer(byId.getVehicleManufacturer());
        vehicleResponse.setTransporter(byId.getTransporter().toString());
        vehicleResponse.setFitnessUpto(byId.getFitnessUpto());
        vehicleResponse.setVehicleType(byId.getVehicleType());
        return vehicleResponse;
    }

    //   @Override
//    public String updateVehicle(VehicleMasterDto vehicleMasterDto) {
//        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(vehicleMasterDto.getVehicleNo()).get();
//        vehicleMaster.setWheelsNo(vehicleMasterDto.getWheelsNo());
//        vehicleMaster.setVehicleType(vehicleMasterDto.getVehicleType());
//        vehicleMaster.setVehicleManufacturer(vehicleMasterDto.getVehicleManufacturer());
//        vehicleMaster.setTransporter(vehicleMasterDto.getTransporterMasters());
//        vehicleMaster.setFitnessUpto(vehicleMasterDto.getFitnessUpto());
//        vehicleMaster.setLoadCapacity(vehicleMasterDto.getLoadCapacity());
//        vehicleMaster.setTareWeight(vehicleMasterDto.getTareWeight());
//        HttpSession session = request.getSession();
//        LocalDateTime dateTime=LocalDateTime.now();
//        vehicleMaster.setModifiedBy(String.valueOf(session.getAttribute("userId")));
//        vehicleMaster.setModifiedDate(dateTime);
//        vehicleMasterRepository.save(vehicleMaster);
//        return "vehicle upadated successfully";
//    }

//    @Override
//    public String deleteVehicle(String vehicleNo) {
//        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(vehicleNo).get();
//        if(vehicleMaster.getStatus().equals("active")) {
//            vehicleMaster.setStatus("inactive");
//            vehicleMasterRepository.save(vehicleMaster);
//            return "vehicle deleted successfully";
//        }
//        else {
//            throw new ResourceNotFoundException("VehicleMaster","vehicle",vehicleNo);
//        }
//    }


}