package com.example.carparking.repository;

import com.example.carparking.entity.CarParkAvailability;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public interface CarParkingAvailabilityRepo extends JpaRepository<CarParkAvailability, Long> {
    List<CarParkAvailability> findByCarparkNumber(String carparkNumber);

    @Query(value = "SELECT * FROM car_park_availability WHERE carpark_number IN :carparkNumbers", nativeQuery = true)
    List<CarParkAvailability> findByCarparkNumbers(List<String> carparkNumbers);
}
