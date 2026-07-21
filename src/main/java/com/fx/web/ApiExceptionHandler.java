package com.fx.web;

import com.fx.rates.UnknownRatePairException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Global error handling — a customer never sees a Java stack trace.
 * BASELINE: bad input (an IllegalArgumentException) -> 400 with a clean JSON message.
 *
 * We deliberately do NOT add a catch-all {@code @ExceptionHandler(Exception.class)}: that
 * would swallow Spring's own web exceptions (e.g. NoResourceFoundException) and turn honest
 * 404s into 500s. Unexpected errors are handled by Spring Boot's default error path, which —
 * with server.error.include-stacktrace=never / include-message=never in application.properties
 * — already returns a clean response with no stack trace to the browser.
 *
 * When you build the "validation & error handling" requirement, EXTEND this class:
 * add a 404 for an unknown currency pair, field-level validation messages, etc.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage() == null ? "bad request" : ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> missingParameter(MissingServletRequestParameterException ex) {
        return Map.of("error", ex.getParameterName() + " is required");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidParameter(MethodArgumentTypeMismatchException ex) {
        if ("amount".equals(ex.getName())) {
            return Map.of("error", "amount must be numeric");
        }
        return Map.of("error", ex.getName() + " is invalid");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> unreadableBody(HttpMessageNotReadableException ex) {
        return Map.of("error", "request body is invalid");
    }

    @ExceptionHandler(UnknownRatePairException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(UnknownRatePairException ex) {
        return Map.of("error", ex.getMessage() == null ? "not found" : ex.getMessage());
    }
}
