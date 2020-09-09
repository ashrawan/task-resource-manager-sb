package com.lk.taskmanager.services.domain.task.dtos;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class TaskSubmissionDTO {

    private Long taskId;

    private List<ResourceInfoEntity> resources;

    private String submissionDescription;
}
