package com.makefire.anonymous.rest.core;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApiError {

    @Schema(description = "오류 메시지", required = true)
    private final String message;

    @Schema(description = "HTTP 오류코드", required = true)
    private final int status;

    ApiError(Throwable throwable, HttpStatus status) {
        this(compatibleMessage(throwable), status);
    }

    /** Keep the public error envelope stable across Spring's exception-message changes. */
    private static String compatibleMessage(Throwable error) {
        if (error instanceof org.springframework.web.HttpRequestMethodNotSupportedException e) {
            return "Request method '" + e.getMethod() + "' not supported";
        }
        if (error instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
            String type = e.getRequiredType() == null ? "unknown" : e.getRequiredType().getName();
            Throwable cause = e.getMostSpecificCause();
            return "Failed to convert value of type '" + (e.getValue() == null ? "null" : e.getValue().getClass().getName())
                    + "' to required type '" + type + "'; nested exception is " + cause;
        }
        if (error instanceof org.springframework.transaction.TransactionSystemException e
                && e.getCause() instanceof jakarta.persistence.RollbackException) {
            // Hibernate 7 appends provider validation details that were not part of the public error.
            String detail = e.getCause().getMessage();
            int providerDetails = detail.indexOf(" [");
            if (providerDetails >= 0) {
                detail = detail.substring(0, providerDetails);
            }
            return e.getMessage() + "; nested exception is javax.persistence.RollbackException: " + detail;
        }
        if (error instanceof org.springframework.dao.InvalidDataAccessApiUsageException e
                && e.getCause() instanceof IllegalArgumentException) {
            String message = e.getMessage();
            if ("The given id must not be null".equals(message)) {
                message += "!";
            }
            return message + "; nested exception is java.lang.IllegalArgumentException: " + message;
        }
        if (error instanceof org.springframework.http.converter.HttpMessageNotReadableException
                && error.getCause() instanceof tools.jackson.core.exc.UnexpectedEndOfInputException e) {
            return "JSON parse error: " + e.getOriginalMessage()
                    + "; nested exception is tools.jackson.core.exc.UnexpectedEndOfInputException: " + e.getOriginalMessage()
                    + "\n at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: "
                    + e.getLocation().getLineNr() + ", column: " + e.getLocation().getColumnNr() + "]";
        }
        return error.getMessage();
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