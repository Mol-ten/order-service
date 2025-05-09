package kz.molten.techshop.orderservice.api.exception.handler;

import kz.molten.techshop.orderservice.api.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderIllegalAccessException.class)
    public ResponseEntity<String> handleOrderIllegalAccessException(OrderIllegalAccessException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalPaymentStatusException.class)
    public ResponseEntity<String> handleIllegalPaymentStatusException(IllegalPaymentStatusException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalOrderStatusException.class)
    public ResponseEntity<String> handleIllegalOrderStatusException(IllegalOrderStatusException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(InsufficientOrderProductQuantityException.class)
    public ResponseEntity<String> handleInsufficientOrderProductQuantityException(InsufficientOrderProductQuantityException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(UserInsufficientRoleException.class)
    public ResponseEntity<String> handleUserInsufficientRoleException(UserInsufficientRoleException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(OrderHistoryNotFoundException.class)
    public ResponseEntity<String> handleOrderHistoryNotFoundException(OrderHistoryNotFoundException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(WebClientInternalServerException.class)
    public ResponseEntity<String> handleWebClientInternalServerException(WebClientInternalServerException exception) {
        log.error("WebClient Internal Server exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(WebClientUsersideException.class)
    public ResponseEntity<String> handleWebClientUsersideException(WebClientUsersideException exception) {
        log.error("WebClient Userside exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFoundException(OrderNotFoundException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
