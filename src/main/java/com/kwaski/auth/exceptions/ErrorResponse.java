package com.kwaski.auth.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse {
    private String message;
    private String error;
    private Date timestamp;

    public ErrorResponse(Exception exception) {
        this.message = exception.getMessage();
        this.error = exception.getClass().getSimpleName();
        this.timestamp = new Date();
    }

}

