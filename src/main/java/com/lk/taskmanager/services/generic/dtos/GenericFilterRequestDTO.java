package com.lk.taskmanager.services.generic.dtos;

import lombok.Data;

@Data
public class GenericFilterRequestDTO<T> {

    private T dataFilter;
}
