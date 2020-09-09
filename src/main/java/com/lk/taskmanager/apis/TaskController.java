package com.lk.taskmanager.apis;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.task.TaskService;
import com.lk.taskmanager.services.domain.task.dtos.TaskFilterDTO;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.UnAuthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("rest/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllTask(Pageable pageable) {
        log.info("Task API: get all task");
        GenericResponseDTO<?> genericResponse = taskService.getAllTasks(pageable);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/date-interval")
    public ResponseEntity<?> getAllUserTasksBetweenDates(Pageable pageable, @RequestBody TaskFilterDTO taskFilterDTO) {
        log.info("Task API: get all user task by userId: ", taskFilterDTO.getUserId());
        boolean hasAdminAccessOrIsOwner = ExtractAuthUser.hasAdminAccessOrIsOwner(taskFilterDTO.getUserId());
        if (!hasAdminAccessOrIsOwner) {
            throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
        }
        taskFilterDTO.setUserId(taskFilterDTO.getUserId());
        GenericResponseDTO<List<TaskEntity>> genericResponse = taskService.getAllUserTasksBetweenDates(pageable, taskFilterDTO);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        log.info("Task API: get task by id");
        GenericResponseDTO<TaskEntity> genericResponse = taskService.getTaskById(id);
        authenticateUserCanAccessTask(genericResponse.getResponse());
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskEntity userEntity) {
        log.info("Task API: create task");
        GenericResponseDTO<TaskEntity> genericResponse = taskService.createTask(userEntity);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody TaskEntity taskEntity) {
        log.info("Task API: update task");
        boolean isManagingTask = isUserRequestingManagingTask(taskEntity);
        if (!(ExtractAuthUser.isAdmin() || isManagingTask)) {
            throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
        }
        GenericResponseDTO<TaskEntity> genericResponse = taskService.updateTask(taskEntity);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/filter")
    public ResponseEntity<?> filterTaskData(@RequestBody GenericFilterRequestDTO<TaskEntity> genericFilterRequest, Pageable pageable) {
        log.info("Task API: Filter user data");
        authenticateUserCanAccessTask(genericFilterRequest.getDataFilter());
        GenericResponseDTO<?> genericResponse = taskService.filterTaskData(genericFilterRequest, pageable);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    //    @PreAuthorize("#taskEntity.assignedTo.userId == authentication.principal.id")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/submit")
    public ResponseEntity<?> submitTask(@RequestBody TaskEntity taskEntity) {

        log.info("Task API: submit task");
        boolean isOwnTask = isUserRequestingOwnTask(taskEntity);
        if (!isOwnTask) {
            throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
        }
        GenericResponseDTO<?> genericResponse = taskService.submitTask(taskEntity);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    /**
     * Check authentication, if user can access task
     * Allow only - if admin OR isManagingTask OR isAssignedToTask
     *
     * @param taskEntity
     * @required TaskEntity value - assignedTo OR reportingTo OR must be Admin
     */
    private void authenticateUserCanAccessTask(TaskEntity taskEntity) {
        boolean isAdmin = ExtractAuthUser.isAdmin();
        if (!isAdmin) {
            boolean isManagingTask = isUserRequestingManagingTask(taskEntity);
            boolean isAssignedToTask = isUserRequestingOwnTask(taskEntity);
            if (isManagingTask || isAssignedToTask) {
                return;
            }
            throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
        }
    }

    private boolean isUserRequestingOwnTask(TaskEntity taskEntity) {
        Long assignedTo = Optional.ofNullable(taskEntity.getAssignedTo())
                .map(UserEntity::getId)
                .orElse(null);
        if (assignedTo != null) {
            return ExtractAuthUser.isOwner(assignedTo);
        }
        return false;
    }

    private boolean isUserRequestingManagingTask(TaskEntity taskEntity) {
        Long reportingTo = Optional.ofNullable(taskEntity.getReportTo())
                .map(UserEntity::getId)
                .orElse(null);
        if (reportingTo != null) {
            return ExtractAuthUser.isOwner(reportingTo);
        }
        return false;
    }

}
