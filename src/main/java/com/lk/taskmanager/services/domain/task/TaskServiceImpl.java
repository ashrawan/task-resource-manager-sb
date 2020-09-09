package com.lk.taskmanager.services.domain.task;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.TaskRepository;
import com.lk.taskmanager.repository.UserRepository;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.task.dtos.TaskFilterDTO;
import com.lk.taskmanager.services.domain.task.dtos.TaskSubmissionDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import com.lk.taskmanager.services.generic.MessageCodeUtil;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<TaskEntity> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public List<TaskEntity> getAllUserTasksBetweenDates(Pageable pageable, TaskFilterDTO taskFilterDTO) {
        LocalDate fromDate = taskFilterDTO.getDateFrom().toLocalDate();
        LocalDate toDate = taskFilterDTO.getDateTo().toLocalDate();
        Long userId = taskFilterDTO.getUserId();
        return taskRepository.searchUsersTaskByDateBetween(pageable, fromDate, toDate, userId);
    }

    @Override
    public TaskEntity getTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return taskEntity;
    }

    @Override
    public TaskEntity createTask(TaskEntity reqTaskEntity) {
        TaskEntity taskEntity = new TaskEntity();
        if(reqTaskEntity.getAssignedUser() != null && reqTaskEntity.getAssignedUser().getId() != null) {
            UserEntity userEntity = userRepository.findById(reqTaskEntity.getAssignedUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
            taskEntity.setAssignedUser(userEntity);
        }
        taskEntity.setAssignedBy(ExtractAuthUser.getCurrentUser());
        taskEntity.setTitle(reqTaskEntity.getTitle());
        taskEntity.setDescription(reqTaskEntity.getDescription());
        taskEntity.setStartDate(reqTaskEntity.getStartDate());
        taskEntity.setEndDate(reqTaskEntity.getEndDate());
        taskEntity.setStatus(reqTaskEntity.getStatus());
        return taskEntity;
    }

    @Override
    public TaskEntity updateTask(TaskEntity reqTaskEntity) {
        TaskEntity taskEntity = taskRepository.findById(reqTaskEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));

        if(reqTaskEntity.getAssignedUser() != null && reqTaskEntity.getAssignedUser().getId() != null) {
            UserEntity userEntity = userRepository.findById(reqTaskEntity.getAssignedUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
            taskEntity.setAssignedUser(userEntity);
        }
        taskEntity.setUpdatedBy(ExtractAuthUser.getCurrentUser());
        taskEntity.setTitle(reqTaskEntity.getTitle());
        taskEntity.setDescription(reqTaskEntity.getDescription());
        taskEntity.setStartDate(reqTaskEntity.getStartDate());
        taskEntity.setEndDate(reqTaskEntity.getEndDate());
        taskEntity.setStatus(reqTaskEntity.getStatus());
        return null;
    }

    @Override
    public GenericResponseDTO<?> submitTask(TaskSubmissionDTO taskSubmissionDTO) {
        return ResponseBuilder.buildFailureResponse(MessageCodeUtil.IMPLEMENTATION_NOT_AVAILABLE);
    }
}
