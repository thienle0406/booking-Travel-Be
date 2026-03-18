package com.mytour.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Thường trả về mã HTTP 400 Bad Request
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}