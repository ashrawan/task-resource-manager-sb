package com.lk.taskmanager.services.generic.dtos;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GenericResponseDTO<T> {

    private String messageCode;

    private T response;

    private long totalElements;

    private HttpStatus httpStatus;

}
