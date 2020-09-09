package com.lk.taskmanager.services.domain.task.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskFilterDTO {

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

    private Long userId;
}
