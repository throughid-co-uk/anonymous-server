package com.makefire.anonymous.config.web.advice;

import com.makefire.anonymous.exception.BadRequestException;
import com.makefire.anonymous.exception.DuplicateException;
import com.makefire.anonymous.exception.ModelNotFoundException;
import com.makefire.anonymous.rest.RestSupport;
import com.makefire.anonymous.rest.core.ApiError;
import com.makefire.anonymous.rest.core.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RestSupport {

    @ExceptionHandler(value = {BadRequestException.class, IllegalArgumentException.class, IndexOutOfBoundsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<?> handleBadRequest(Exception e) {
        log.info("BadRequest Exception, message: {{}}", e.getMessage());
        return ApiResult.ERROR(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<?> unAuthorized(Exception e, HttpServletResponse response) {
        log.info("AuthenticationException Exception, message: {{}}", e.getMessage());
        return ApiResult.ERROR(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<?> gone(Exception e) {
        log.info("AccessDeniedException Exception, message: {{}}", e.getMessage());
        return ApiResult.ERROR(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {ModelNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<?> handleNotFound(Exception e) {
        log.info("ModelNotFoundException Exception, message: {{}}", e.getMessage());
        return ApiResult.ERROR(e, HttpStatus.NOT_FOUND);
    }

    /** Keep pre-Boot-3 missing-resource responses out of the catch-all API error handler. */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public java.util.Map<String, Object> missingResource(
            org.springframework.web.servlet.resource.NoResourceFoundException e,
            jakarta.servlet.http.HttpServletRequest request) {
        java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("timestamp", java.time.Instant.now().toString());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("path", request.getRequestURI());
        return body;
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleInternalServerError(Exception e) {
        log.info("Exception, message: {{}}", e.getMessage());
        return ApiResult.ERROR(legacyErrorMessage(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** Preserve the public error text exposed by the previous Spring MVC/JPA versions. */
    private String legacyErrorMessage(Exception e) {
        if (e instanceof org.springframework.web.HttpRequestMethodNotSupportedException method) {
            return "Request method '" + method.getMethod() + "' not supported";
        }
        if (e instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException mismatch) {
            return "Failed to convert value of type '" + mismatch.getValue().getClass().getName()
                    + "' to required type '" + mismatch.getRequiredType().getName()
                    + "'; nested exception is " + mismatch.getCause();
        }
        if (e instanceof org.springframework.transaction.TransactionSystemException
                && e.getCause() instanceof jakarta.persistence.RollbackException) {
            return e.getMessage() + "; nested exception is javax.persistence.RollbackException: Error while committing the transaction";
        }
        if (e instanceof org.springframework.dao.InvalidDataAccessApiUsageException
                && "The given id must not be null".equals(e.getMessage())) {
            return "The given id must not be null!; nested exception is java.lang.IllegalArgumentException: The given id must not be null!";
        }
        if (e instanceof org.springframework.http.converter.HttpMessageNotReadableException
                && e.getCause() instanceof tools.jackson.core.exc.StreamReadException parse) {
            String detail = parse.getOriginalMessage();
            var location = parse.getLocation();
            return "JSON parse error: " + detail
                    + "; nested exception is " + parse.getClass().getName() + ": " + detail
                    + "\n at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: "
                    + location.getLineNr() + ", column: " + location.getColumnNr() + "]";
        }
        return e.getMessage();
    }

    @ExceptionHandler(value = {DuplicateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResult<ApiError> handleConflict(Exception e) {
        log.info("Conflict Exception, message: {{}}", e.getMessage());
        return ApiResult.ERROR(e, HttpStatus.CONFLICT);
    }
}