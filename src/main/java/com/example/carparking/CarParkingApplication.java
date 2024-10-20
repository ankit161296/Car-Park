package com.example.carparking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.carparking")
@EntityScan(basePackages = "com.example.carparking")
public class CarParkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarParkingApplication.class, args);
    }

}
