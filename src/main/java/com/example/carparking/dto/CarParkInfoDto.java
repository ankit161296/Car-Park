package com.example.carparking.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming( PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CarParkInfoDto {
    private String totalLots;
    private String lotType;
    private String lotsAvailable;
}
