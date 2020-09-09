package com.lk.taskmanager.services.generic;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GenericResponseDTO<T> {

    private String messageCode;

    private T response;

    private HttpStatus httpStatus;

}
