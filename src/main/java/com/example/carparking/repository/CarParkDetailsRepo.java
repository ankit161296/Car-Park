package com.example.carparking.repository;

import com.example.carparking.entity.CarParkDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CarParkDetailsRepo extends JpaRepository<CarParkDetails, Long> {

    @Query(value="SELECT *, ST_Distance(geom, ST_MakePoint(:longitude, :latitude)) AS distance " +
            "FROM car_park_details " +
            "WHERE ST_DWithin(geom, ST_MakePoint(:longitude, :latitude), :maxDistance) " +
            "ORDER BY distance " +
            "LIMIT :size OFFSET :offset;", nativeQuery = true)
    List<CarParkDetails> findNearestCarParks(double latitude, double longitude, int size, long offset, double maxDistance);
}
