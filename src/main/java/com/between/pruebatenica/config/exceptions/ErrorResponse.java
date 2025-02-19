package com.between.pruebatenica.config.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class ErrorResponse {

    private int statusCode;
    private String message;
    private String details;
    private LocalDateTime timestamp;

}
