package com.prashanth.book.handler;

import com.prashanth.book.exception.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ==============================
    // Exception Handlers
    // ==============================

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
                BusinessErrorCodes.ACCOUNT_LOCKED,
                exp.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
                BusinessErrorCodes.ACCOUNT_DISABLED,
                exp.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
                BusinessErrorCodes.BAD_CREDENTIALS,
                BusinessErrorCodes.BAD_CREDENTIALS.getDescription());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        log.error("Messaging exception occurred", exp);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, exp.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(errors)
                        .build());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return buildResponse(HttpStatus.FORBIDDEN, null, exp.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException exp) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                null,
                exp.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalStateException exp) {
        log.error("Illegal state encountered", exp);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                exp.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        log.error("Unhandled exception occurred", exp);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                "Internal server error, contact admin");
    }

    // ==============================
    // Helper Method
    // ==============================

    private ResponseEntity<ExceptionResponse> buildResponse(
            HttpStatus status,
            BusinessErrorCodes errorCode,
            String errorMessage
    ) {
        ExceptionResponse.ExceptionResponseBuilder builder = ExceptionResponse.builder()
                .error(errorMessage);

        if (errorCode != null) {
            builder.businessErrorCode(errorCode.getCode())
                    .businessErrorDescription(errorCode.getDescription());
        }

        return ResponseEntity.status(status).body(builder.build());
    }
}
