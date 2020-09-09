package com.lk.taskmanager.services.domain.task;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.services.domain.task.dtos.TaskFilterDTO;
import com.lk.taskmanager.services.domain.task.dtos.TaskSubmissionDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    Page<TaskEntity> getAllTasks(Pageable pageable);

    List<TaskEntity> getAllUserTasksBetweenDates(Pageable pageable, TaskFilterDTO taskFilterDTO);

    TaskEntity getTaskById(Long id);

    TaskEntity createTask(TaskEntity TaskEntity);

    TaskEntity updateTask(TaskEntity TaskEntity);

    GenericResponseDTO<?> submitTask(TaskSubmissionDTO taskSubmissionDTO);

}
