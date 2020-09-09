package com.lk.taskmanager.services.domain.task;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.services.domain.task.dtos.TaskFilterDTO;
import com.lk.taskmanager.services.domain.task.dtos.TaskSubmissionDTO;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    GenericResponseDTO<List<TaskEntity>> getAllTasks(Pageable pageable);

    GenericResponseDTO<List<TaskEntity>> getAllUserTasksBetweenDates(Pageable pageable, TaskFilterDTO taskFilterDTO);

    GenericResponseDTO<TaskEntity> getTaskById(Long id);

    GenericResponseDTO<TaskEntity> createTask(TaskEntity TaskEntity);

    GenericResponseDTO<TaskEntity> updateTask(TaskEntity TaskEntity);

    GenericResponseDTO<List<TaskEntity>> filterTaskData(GenericFilterRequestDTO<TaskEntity> genericFilterRequestDTO, Pageable pageable);

    GenericResponseDTO<?> submitTask(TaskEntity reqTaskEntity);

}
