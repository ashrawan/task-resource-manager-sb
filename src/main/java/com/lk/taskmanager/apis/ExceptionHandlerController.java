package com.lk.taskmanager.apis;

import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.MessageCodeUtil;
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
        GenericResponseDTO<String> genericResponse = ResponseBuilder.buildResponse(MessageCodeUtil.NOT_FOUND, ex.getMessage());
        genericResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final HttpServletRequest request) {

        log.info("MethodArgumentTypeMismatchException handled");
        GenericResponseDTO<String> genericResponse = ResponseBuilder.buildResponse(MessageCodeUtil.REQUEST_MISMATCH, ex.getMessage());
        genericResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @ExceptionHandler(UnAuthorizedAccessException.class)
    public ResponseEntity<?> unAuthorizedAccessException(final UnAuthorizedAccessException ex, final HttpServletRequest request) {

        log.info("UnAuthorizedChannelAccessException handled");
        GenericResponseDTO<String> genericResponse = ResponseBuilder.buildResponse(MessageCodeUtil.UNAUTHORIZED, ex.getMessage());
        genericResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @ExceptionHandler(LKAppException.class)
    public ResponseEntity<?> globalAppException(final LKAppException ex, final HttpServletRequest request) {

        log.info("LKAppException handled", ex.getMessage());
        GenericResponseDTO<String> genericResponse = ResponseBuilder.buildResponse(MessageCodeUtil.UNKNOWN_ERROR, ex.getMessage());
        genericResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> globalAppException(final RuntimeException ex, final HttpServletRequest request) {

        log.info("Runtime Exception occured", ex.getMessage());
        ex.printStackTrace();
        GenericResponseDTO<String> genericResponse = ResponseBuilder.buildResponse(MessageCodeUtil.UNKNOWN_ERROR, ex.getMessage());
        genericResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }
}
