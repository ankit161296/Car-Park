package com.example.carparking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "car_park_availability")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarParkAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String totalLots;
    private String lotType;
    private String lotsAvailable;
    private String carparkNumber;

}
