package com.sabinghost19.teamslkghostapp.exceptions;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.RegisterUserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        System.out.println("Global exception: " + ex.getMessage());
        return new ResponseEntity<>("Error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<RegisterUserResponseDTO> handlePasswordMismatchException(PasswordMismatchException ex) {
        RegisterUserResponseDTO response = RegisterUserResponseDTO.builder()
                .message(ex.getMessage())
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<RegisterUserResponseDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        RegisterUserResponseDTO response = RegisterUserResponseDTO.builder()
                .message(ex.getMessage())
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}