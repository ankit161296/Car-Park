package com.example.carparking.controller;

import com.example.carparking.dto.CarParksResponseDto;
import com.example.carparking.dto.ResponseDto;
import com.example.carparking.service.CarParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CarParkingController {

    @Autowired
    private CarParkingService carParkingService;


    @GetMapping("/save-car-park-availability")
    public ResponseEntity<String> saveCarparkAvailability() {
        carParkingService.saveCarParkAvailabilityToDB();
       return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/save-car-park-info")
    public ResponseEntity<String> saveCarparkInfo() {
        carParkingService.saveCarParkInfoToDB();
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/carparks/nearest")
    public ResponseEntity<ResponseDto> getCarParksNearest(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int  page,
            @RequestParam("per_page") int size) {
        System.out.println("latitude: " + latitude);
        List<CarParksResponseDto> responseDtos = carParkingService.getNearestCarParks(latitude,longitude,page,size);
        return new ResponseEntity<>(new ResponseDto("Successfully fetched Data", true, responseDtos, HttpStatus.OK.toString()), HttpStatus.OK);
    }

}
