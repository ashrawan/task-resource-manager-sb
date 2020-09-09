package com.lk.taskmanager.services.domain.task;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.ResourceInfoRepository;
import com.lk.taskmanager.repository.TaskRepository;
import com.lk.taskmanager.repository.UserRepository;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.task.dtos.TaskFilterDTO;
import com.lk.taskmanager.services.generic.GenericSpecification;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ResourceInfoRepository resourceInfoRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ResourceInfoRepository resourceInfoRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.resourceInfoRepository = resourceInfoRepository;
    }

    @Override
    public GenericResponseDTO<List<TaskEntity>> getAllTasks(Pageable pageable) {
        Page<TaskEntity> taskEntityPage = taskRepository.findAll(pageable);
        return ResponseBuilder.buildPagedSuccessResponse(taskEntityPage);
    }

    @Override
    public GenericResponseDTO<List<TaskEntity>> getAllUserTasksBetweenDates(Pageable pageable, TaskFilterDTO taskFilterDTO) {
        LocalDate fromDate = taskFilterDTO.getDateFrom().toLocalDate();
        LocalDate toDate = taskFilterDTO.getDateTo().toLocalDate();
        Long userId = taskFilterDTO.getUserId();
        List<TaskEntity> taskEntities = taskRepository.searchUsersTaskByDateBetween(pageable, fromDate, toDate, userId);
        return ResponseBuilder.buildSuccessResponse(taskEntities, taskEntities.size());
    }

    @Override
    public GenericResponseDTO<TaskEntity> getTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TASK_RECORD_NOT_FOUND));
        return ResponseBuilder.buildSuccessResponse(taskEntity);
    }

    @Override
    public GenericResponseDTO<TaskEntity> createTask(TaskEntity reqTaskEntity) {
        TaskEntity taskEntity = new TaskEntity();

        UserEntity assignedToUser = getRequestApiUserEntity(reqTaskEntity.getAssignedTo());
        taskEntity.setAssignedTo(assignedToUser);
        UserEntity reportToUser = getRequestApiUserEntity(reqTaskEntity.getReportTo());
        taskEntity.setReportTo(reportToUser);

        taskEntity.setCreatedBy(ExtractAuthUser.getCurrentUser());
        taskEntity.setTitle(reqTaskEntity.getTitle());
        taskEntity.setDescription(reqTaskEntity.getDescription());
        taskEntity.setStartDate(reqTaskEntity.getStartDate());
        taskEntity.setEndDate(reqTaskEntity.getEndDate());
        taskEntity.setStatus(reqTaskEntity.getStatus());
        taskRepository.save(taskEntity);
        return ResponseBuilder.buildSuccessResponse(taskEntity);
    }

    @Override
    public GenericResponseDTO<TaskEntity> updateTask(TaskEntity reqTaskEntity) {
        TaskEntity taskEntity = taskRepository.findById(reqTaskEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TASK_RECORD_NOT_FOUND));

        UserEntity assignedToUser = getRequestApiUserEntity(reqTaskEntity.getAssignedTo());
        taskEntity.setAssignedTo(assignedToUser);
        UserEntity reportToUser = getRequestApiUserEntity(reqTaskEntity.getReportTo());
        if(taskEntity.getReportTo().getId() != reportToUser.getId()) {
            taskEntity.setReportTo(reportToUser);
            Set<UserEntity> accessList = new HashSet<>();
            accessList.add(taskEntity.getReportTo());
            validateTaskResourcesAndUpdateAccess(taskEntity, accessList);
        }

        taskEntity.setUpdatedBy(ExtractAuthUser.getCurrentUser());
        taskEntity.setTitle(reqTaskEntity.getTitle());
        taskEntity.setDescription(reqTaskEntity.getDescription());
        taskEntity.setStartDate(reqTaskEntity.getStartDate());
        taskEntity.setEndDate(reqTaskEntity.getEndDate());
        taskEntity.setStatus(reqTaskEntity.getStatus());
        taskRepository.save(taskEntity);
        return ResponseBuilder.buildSuccessResponse(taskEntity);
    }

    @Override
    public GenericResponseDTO<List<TaskEntity>> filterTaskData(GenericFilterRequestDTO<TaskEntity> genericFilterRequest, Pageable pageable) {
        Specification<TaskEntity> specification = Specification.where(TaskSearchSpecification.processDynamicTaskFilter(genericFilterRequest))
                .and(Specification.where(TaskSearchSpecification.getTaskAssignedToUser(genericFilterRequest)));
        Page<TaskEntity> filteredTasks = taskRepository.findAll(specification, pageable);
        GenericResponseDTO<List<TaskEntity>> genericResponse = ResponseBuilder.buildSuccessResponse(filteredTasks.getContent(), filteredTasks.getTotalElements());
        return genericResponse;
    }

    @Override
    public GenericResponseDTO<?> submitTask(TaskEntity reqTaskEntity) {
        TaskEntity taskEntity = taskRepository.findById(reqTaskEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.TASK_RECORD_NOT_FOUND));
        Set<UserEntity> accessList = new HashSet<>();
        accessList.add(taskEntity.getReportTo());
        validateTaskResourcesAndUpdateAccess(reqTaskEntity, accessList);
        taskEntity.setTaskResources(reqTaskEntity.getTaskResources());
        taskEntity.setSubmissionDescription(reqTaskEntity.getSubmissionDescription());
        if (taskEntity.getSubmittedDate() == null) {
            taskEntity.setSubmittedDate(LocalDateTime.now());
            taskEntity.setReSubmittedDate(null);
        } else {
            taskEntity.setReSubmittedDate(LocalDateTime.now());
        }
        taskRepository.save(taskEntity);
        return ResponseBuilder.buildSuccessResponse(taskEntity);
    }

    private void validateTaskResourcesAndUpdateAccess(TaskEntity reqTaskEntity, Set<UserEntity> accessList) {
        if (reqTaskEntity.getTaskResources() != null) {
            Set<ResourceInfoEntity> resourceList = reqTaskEntity.getTaskResources().stream().map(resourceInfoEntity -> {
                ResourceInfoEntity retResourceInfoEntity = resourceInfoRepository.findById(resourceInfoEntity.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND));
                retResourceInfoEntity.setUserAccessList(accessList);
                resourceInfoRepository.save(retResourceInfoEntity);
                return retResourceInfoEntity;
            }).collect(Collectors.toSet());
            reqTaskEntity.setTaskResources(resourceList);
        } else {
            reqTaskEntity.setTaskResources(null);
        }
    }

    private UserEntity getRequestApiUserEntity(UserEntity reqUserEntity) {
        if (reqUserEntity == null || reqUserEntity.getId() == null) {
            throw new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findById(reqUserEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return userEntity;
    }

}
