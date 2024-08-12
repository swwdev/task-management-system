package com.example.taskmanagment.handler;


import com.example.taskmanagment.dto.response.ExceptionResponse;
import com.example.taskmanagment.exceptions.CantAssignException;
import com.example.taskmanagment.exceptions.CantObtainFingerPrintException;
import com.example.taskmanagment.exceptions.IllegalIdException;
import com.example.taskmanagment.exceptions.InvalidQueryParamsException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(CantObtainFingerPrintException.class)
    public ResponseEntity<ExceptionResponse> handleException(CantObtainFingerPrintException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", ", "[", "]"))
        );
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, NOT_FOUND);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UsernameNotFoundException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, NOT_FOUND);
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse> handleException(JwtException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessDeniedException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleException(AuthenticationException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, UNAUTHORIZED);
    }


    @ExceptionHandler(DataSourceLookupFailureException.class)
    public ResponseEntity<ExceptionResponse> handleException(DataSourceLookupFailureException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalIdException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalIdException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(InvalidQueryParamsException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidQueryParamsException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(CantAssignException.class)
    public ResponseEntity<ExceptionResponse> handleException(CantAssignException exception) {
        ExceptionResponse response = new ExceptionResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response, BAD_REQUEST);
    }


}
