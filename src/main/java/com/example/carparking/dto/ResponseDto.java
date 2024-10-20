package com.example.carparking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    private String message;
    private boolean success;
    private Object data;
    private String httpStatus;
}
