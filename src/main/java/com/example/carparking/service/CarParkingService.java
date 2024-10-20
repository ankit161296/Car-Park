package com.example.carparking.service;

import com.example.carparking.dto.CarParksResponseDto;
import com.example.carparking.repository.CarParkingAvailabilityRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public interface CarParkingService {
    void saveCarParkAvailabilityToDB();

    void saveCarParkInfoToDB();

    List<CarParksResponseDto> getNearestCarParks(double latitude, double longitude, int page, int size);
    List<CarParksResponseDto> getNearestCarParksV2(double latitude, double longitude, int page, int size);

}
