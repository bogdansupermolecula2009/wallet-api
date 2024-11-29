package ru.andrianov.wallet_api.controllers;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andrianov.wallet_api.exceptions.*;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorDto> handleWalletNotFound(WalletNotFoundException ex) {
        return buildErrorDto(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorDto> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleInvalidJson(MethodArgumentNotValidException ex) {
        InvalidJsonException invalidJsonException = new InvalidJsonException("Invalid JSON: walletId, amount, and operationType are required");
        return buildErrorDto(invalidJsonException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleInvalidJsonFormat(HttpMessageNotReadableException ex) {
        InvalidJsonException invalidJsonException = new InvalidJsonException("Invalid JSON format");
        return buildErrorDto(invalidJsonException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        return buildErrorDto("Unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorDto> handleOptimisticLockException(OptimisticLockException ex) {
        return buildErrorDto("Optimistic lock error: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    private ResponseEntity<ErrorDto> buildErrorDto(String message, HttpStatus status) {
        ErrorDto errorDto = new ErrorDto(message, status.value());
        return new ResponseEntity<>(errorDto, status);
    }
}
