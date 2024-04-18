package com.weighbridge.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class VehicleMaster {
    @Id
    private String vehicleNo;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "transporter_vehicle",
            joinColumns = @JoinColumn(name = "vehicleNo", referencedColumnName = "vehicleNo"),
            inverseJoinColumns = @JoinColumn(name = "transporter_id", referencedColumnName = "transporterId")
    )
    private List<TransporterMaster> transporter;

    private String vehicleType;

    private String vehicleManufacturer;

    private Integer wheelsNo;

    private Double tareWeight;

    private Double loadCapacity;

    private Date fitnessUpto;
}