package com.weighbridge.services.impls;

import com.weighbridge.entities.TransporterMaster;
import com.weighbridge.payloads.TransporterRequest;
import com.weighbridge.repsitories.TransporterMasterRepository;
import com.weighbridge.services.TransporterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransporterServiceImpl implements TransporterService {
    @Autowired
    private TransporterMasterRepository transporterMasterRepository;
    @Autowired
    private  ModelMapper modelMapper;
    @Override
    public String addTransporter(TransporterRequest transporterRequest) {
        Boolean BytransporterMaster = transporterMasterRepository.existsByTransporterName(transporterRequest.getTransporterName());
        if (BytransporterMaster){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Transporter already exist");
        }
        else {

            TransporterMaster map = modelMapper.map(transporterRequest, TransporterMaster.class);
            transporterMasterRepository.save(map);
            return "transporter added successfully";
        }

    }

    @Override
    public List<String> getAllTransporter() {
        List<TransporterMaster> all = transporterMasterRepository.findAll();
        List<String> str=new ArrayList<>();
        for(TransporterMaster transporterMaster:all){
           str.add(transporterMaster.getTransporterName());
        }
        return str;
    }
}
