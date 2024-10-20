package com.example.carparking.exception;

import com.example.carparking.dto.CarParksResponseDto;
import com.example.carparking.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler extends Exception {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto> handleException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(e.getMessage(), false, null, HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto> handleException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(e.getMessage(), false, null, HttpStatus.BAD_REQUEST.toString()));
    }

}
