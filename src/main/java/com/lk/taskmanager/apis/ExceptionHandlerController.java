package com.lk.taskmanager.apis;

import com.lk.taskmanager.utils.exceptions.ExceptionResponse;
import com.lk.taskmanager.utils.exceptions.LKAppException;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import com.lk.taskmanager.utils.exceptions.UnAuthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(final ResourceNotFoundException ex, final HttpServletRequest request) {

        log.info("DataNotFoundException handled");
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(ex.getMessage());
        exceptionResponse.setApiUrl(request.getRequestURI());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final HttpServletRequest request) {

        log.info("MethodArgumentTypeMismatchException handled");
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(ex.getMessage());
        exceptionResponse.setApiUrl(request.getRequestURI());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnAuthorizedAccessException.class)
    public ResponseEntity<?> unAuthorizedAccessException(final UnAuthorizedAccessException ex, final HttpServletRequest request) {

        log.info("UnAuthorizedChannelAccessException handled");
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(ex.getMessage());
        exceptionResponse.setApiUrl(request.getRequestURI());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LKAppException.class)
    public ResponseEntity<?> globalAppException(final LKAppException ex, final HttpServletRequest request) {

        log.info("LKAppException handled");
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(ex.getMessage());
        exceptionResponse.setApiUrl(request.getRequestURI());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}