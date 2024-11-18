package com.sendi.spongeurl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidURLException extends RuntimeException{

    public InvalidURLException() {
    }

    public InvalidURLException(String message) {
        super(message);
    }
}
