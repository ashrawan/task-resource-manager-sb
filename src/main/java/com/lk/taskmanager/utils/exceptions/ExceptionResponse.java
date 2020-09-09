package com.lk.taskmanager.utils.exceptions;

import lombok.Data;

@Data
public class ExceptionResponse {

    private String message;
    private String apiUrl;
}