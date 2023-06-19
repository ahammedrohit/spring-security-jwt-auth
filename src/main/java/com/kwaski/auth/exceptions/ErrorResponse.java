package com.kwaski.auth.exceptions;

import lombok.*;

@Data
public class ErrorResponse {

    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
