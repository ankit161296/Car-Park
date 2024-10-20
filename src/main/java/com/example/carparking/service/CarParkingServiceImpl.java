package com.example.carparking.service;


import com.example.carparking.dto.CarParkAvailabilityDto;
import com.example.carparking.dto.CarParkInfoDto;
import com.example.carparking.dto.CarParksResponseDto;
import com.example.carparking.entity.CarParkAvailability;
import com.example.carparking.entity.CarParkDetails;
import com.example.carparking.entity.CarParkingInfo;
import com.example.carparking.repository.CarParkDetailsRepo;
import com.example.carparking.repository.CarParkingAvailabilityRepo;
import com.example.carparking.repository.CarParkingInfoRepo;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarParkingServiceImpl implements CarParkingService{


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CarParkingAvailabilityRepo carParkingAvailabilityRepo;
    @Autowired
    private CarParkingInfoRepo carParkingInfoRepo;
    @Autowired(required = true)
    private CarParkDetailsRepo carParkDetailsRepo;

    @Override
    public void saveCarParkAvailabilityToDB() {
        CarParkAvailabilityDto dataMap= restTemplate.
                getForEntity("https://api.data.gov.sg/v1/transport/carpark-availability", CarParkAvailabilityDto.class)
                .getBody();

        List<CarParkAvailabilityDto.AvailabilityItems> items = dataMap.getItems();
        List<CarParkingInfo> carParkingInfos = new ArrayList<>();
        List<CarParkAvailability> carParkAvailabilities = new ArrayList<>();

        if(Objects.nonNull(items)){
           for(CarParkAvailabilityDto.AvailabilityItems item: items){
               for(CarParkAvailabilityDto.CarparkData carparkData : item.getCarparkData()){
                   carParkingInfos.add(CarParkingInfo.builder()
                           .updateDatetime(carparkData.getUpdateDatetime())
                           .carparkNumber(carparkData.getCarparkNumber())
                           .build());
                   for(CarParkInfoDto carParkInfoDto : carparkData.getCarparkInfo()){
                       carParkAvailabilities.add(CarParkAvailability.builder()
                               .lotsAvailable(
                                       carParkInfoDto.getLotsAvailable())
                                       .carparkNumber(carparkData.getCarparkNumber())
                                       .lotType(carParkInfoDto.getLotType())
                                       .totalLots(carParkInfoDto.getTotalLots())
                               .build());
                   }
               }
            }
        }
        carParkingInfoRepo.saveAll(carParkingInfos);
        carParkingAvailabilityRepo.saveAll(carParkAvailabilities);
    }

    @Override
    public void saveCarParkInfoToDB() { // use batches for api calls
        List<CarParkDetails> dtos = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader("/Users/mmt11340/Downloads/Car Parking/src/HDBCarparkInformation.csv"))) {
            String[] line;
            reader.skip(1); // Skip header row
            int dataLimit = 1000; // Total number of entries to process
            int batchSize = 50; // Number of entries per batch
            List<CarParkDetails> batch = new ArrayList<>();

            while ((line = reader.readNext()) != null && dataLimit-- > 0) {
                CarParkDetails dto = new CarParkDetails();
                dto.setCarParkNo(line[0]);
                dto.setAddress(line[1]);

                // Add to current batch
                batch.add(dto);

                // If batch is full, process it
                if (batch.size() == batchSize) {
                    // Process the batch (e.g., send to a database, API call, etc.)
                    processBatch(batch);

                    // Clear the batch for the next set of entries
                    batch.clear();
                }
            }

            // Process remaining items in the batch (if any)
            if (!batch.isEmpty()) {
                processBatch(batch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void processBatch(List<CarParkDetails> batch) throws InterruptedException {
            // Example: Store batch in the database or send an API request
            // For now, just print the batch size
        System.out.println("Processing batch of size: " + batch.size());
        for (CarParkDetails dto : batch) {
            Map<String, Double> latLongMap = getLatLong(String.valueOf(dto.getLatitude()), String.valueOf(dto.getLongitude()));
            dto.setLatitude(latLongMap.get("latitude"));
            dto.setLongitude(latLongMap.get("longitude"));
        }
        carParkDetailsRepo.saveAll(batch);
        Thread.sleep(2000);
    }

    private Map<String,Double> getLatLong(String x, String y){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0MmI5NjJiZWY5OWE1M2JjOWNjZGE2MWYxMjlkYTg4MiIsImlzcyI6Imh0dHA6Ly9pbnRlcm5hbC1hbGItb20tcHJkZXppdC1pdC1uZXctMTYzMzc5OTU0Mi5hcC1zb3V0aGVhc3QtMS5lbGIuYW1hem9uYXdzLmNvbS9hcGkvdjIvdXNlci9wYXNzd29yZCIsImlhdCI6MTcyOTQxMjIwMSwiZXhwIjoxNzI5NjcxNDAxLCJuYmYiOjE3Mjk0MTIyMDEsImp0aSI6IlZKWWZTZHI3ZHUzRE9MWDUiLCJ1c2VyX2lkIjozNDYzLCJmb3JldmVyIjpmYWxzZX0.2_EtMekgxO0w6_q2Kq1BcXySxVua_h2kUAgUvJBU1kQ");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://www.onemap.gov.sg/api/common/convert/3414to4326?X=").append(x).append("&Y=").append(y);
        Map<String,Double> map= restTemplate.exchange(stringBuilder.toString(), HttpMethod.GET, new HttpEntity<>(null,headers), Map.class).getBody();
        return map;
    }


    public List<CarParksResponseDto> getNearestCarParks(double latitude, double longitude, int page, int size) {
        List<CarParkDetails> carParkDetails = carParkDetailsRepo.findAll();
        CarParkDetails target = new CarParkDetails();
        target.setLatitude(latitude);
        target.setLongitude(longitude);

        carParkDetails.sort(Comparator.comparingDouble(source -> source.distanceTo(target)));
        // add in response list if availability is greater than zero
        List<CarParksResponseDto> carParksResponseDtos = new ArrayList<>();
        for(CarParkDetails details : carParkDetails){
            List<CarParkAvailability> carParkAvailabilities = carParkingAvailabilityRepo.findByCarparkNumber(details.getCarParkNo());
            if(Objects.nonNull(carParkAvailabilities))
             carParkAvailabilities=carParkAvailabilities.stream().filter(
                    carParkAvailability -> !carParkAvailability.getLotsAvailable().equals("0")).collect(Collectors.toList());

            if(!carParkAvailabilities.isEmpty()){
                carParksResponseDtos.add(CarParksResponseDto.builder()
                        .address(details.getAddress())
                        .distance(details.getDistance())
                        .availableLots(carParkAvailabilities.get(0).getLotsAvailable())
                        .totalLots(carParkAvailabilities.get(0).getTotalLots())
                        .longitude(details.getLongitude())
                        .latitude(details.getLatitude())
                        .build());
            }
        }
        int totalCount = carParksResponseDtos.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalCount);
        return carParksResponseDtos.subList(startIndex, endIndex);
    }

    @Override
    public List<CarParksResponseDto> getNearestCarParksV2(double latitude, double longitude, int page, int size) {
        CarParkDetails target = new CarParkDetails();
        target.setLatitude(latitude);
        target.setLongitude(longitude);

        // Step 1: Retrieve only the necessary data (e.g., using pagination)
        Pageable pageable = PageRequest.of(page - 1, size);  // Adjust page size based on input
        List<CarParkDetails> carParkDetailsPage = carParkDetailsRepo.findNearestCarParks(latitude, longitude, pageable.getPageSize(), pageable.getOffset(), 1000);

        // Step 2: Pre-fetch availability for all car parks in a single query
        List<String> carParkNumbers = carParkDetailsPage.stream()
                .map(CarParkDetails::getCarParkNo)
                .collect(Collectors.toList());

        Map<String, CarParkAvailability> availabilityMap = carParkingAvailabilityRepo.findByCarparkNumbers(carParkNumbers)
                .stream()
                .filter(carParkAvailability -> !carParkAvailability.getLotsAvailable().equals("0"))
                .collect(Collectors.toMap(CarParkAvailability::getCarparkNumber, availability -> availability));

        // Step 3: Process car parks in parallel (optional for performance with large datasets)
        List<CarParksResponseDto> carParksResponseDtos = carParkDetailsPage.parallelStream()
                .filter(details -> availabilityMap.containsKey(details.getCarParkNo()))
                .map(details -> {
                    CarParkAvailability availability = availabilityMap.get(details.getCarParkNo());
                    CarParksResponseDto responseDto = new CarParksResponseDto();
                    responseDto.setTotalLots(availability.getTotalLots());
                    responseDto.setAvailableLots(availability.getLotsAvailable());
                    responseDto.setLatitude(details.getLatitude());
                    responseDto.setLongitude(details.getLongitude());
                    responseDto.setAddress(details.getAddress());
                    responseDto.setDistance(details.distanceTo(target));
                    return responseDto;
                })
                .collect(Collectors.toList());

        // Return paginated response (already handled by database pagination)
        return carParksResponseDtos;
    }


}
