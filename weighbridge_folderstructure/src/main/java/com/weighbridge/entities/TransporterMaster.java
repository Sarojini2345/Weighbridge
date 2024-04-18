package com.weighbridge.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransporterMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transporterId;

    private String transporterName;

    private String transporterContactNo;

    private String transporterEmailId;

    private String transporterAddress;

}