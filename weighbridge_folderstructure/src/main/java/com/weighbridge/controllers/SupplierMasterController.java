package com.weighbridge.controllers;


import com.weighbridge.dtos.SupplierMasterDto;
import com.weighbridge.services.SupplierMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierMasterController {

    @Autowired
    private SupplierMasterService supplierMasterService;

    @PostMapping
    public ResponseEntity<SupplierMasterDto> saveSupplierMaster(@RequestBody SupplierMasterDto supplierMasterDto){
        SupplierMasterDto supplier = supplierMasterService.createSupplier(supplierMasterDto);

        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SupplierMasterDto>> getAllSupplier(){
        List<SupplierMasterDto> allSupplier = supplierMasterService.getAllSupplier();
        return new ResponseEntity<>(allSupplier,HttpStatus.OK);
    }
    @GetMapping("/get/list")
    public ResponseEntity<List<String >> getAllSupplierAsString(){
        List<String > allSupplierString = supplierMasterService.getAllSupplierAsString();
        return new ResponseEntity<>(allSupplierString,HttpStatus.OK);
    }
}
