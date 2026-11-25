package com.makefire.anonymous.rest.core;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApiError {

    @Schema(description = "오류 메시지", required = true)
    private final String message;

    @Schema(description = "HTTP 오류코드", required = true)
    private final int status;

    ApiError(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status);
    }

    ApiError(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}