package ru.practicum.mainservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }
}
