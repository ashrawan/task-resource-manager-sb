package com.lk.taskmanager.apis;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.task.TaskService;
import com.lk.taskmanager.services.domain.task.dtos.TaskFilterDTO;
import com.lk.taskmanager.services.domain.task.dtos.TaskSubmissionDTO;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getAllTask(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        log.info("Task API: get all task");
        return new ResponseEntity<>(taskService.getAllTasks(pageable), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/filer")
    public ResponseEntity<?> getAllUserTasksBetweenDates(@PageableDefault(page = 0, size = 10) Pageable pageable, @RequestBody TaskFilterDTO taskFilterDTO) {
        Long userId = ExtractAuthUser.resolveHasAdminAccessOrIsOwner(taskFilterDTO.getUserId());
        log.info("Task API: get all user task by userId: ", userId);
        taskFilterDTO.setUserId(userId);
        return new ResponseEntity<>(taskService.getAllUserTasksBetweenDates(pageable, taskFilterDTO), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        log.info("Task API: get task by id");
        return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskEntity userEntity) {
        log.info("Task API: create task");
        return new ResponseEntity<>(taskService.createTask(userEntity), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody TaskEntity userEntity) {
        log.info("Task API: update task");
        return new ResponseEntity<>(taskService.updateTask(userEntity), HttpStatus.OK);
    }

    @PreAuthorize("#taskSubmission.userId == authentication.principal.id")
    @PutMapping("/submit")
    public ResponseEntity<?> submitTask(@RequestBody TaskSubmissionDTO taskSubmission) {

        log.info("Task API: update user password");
        GenericResponseDTO<?> genericResponse = taskService.submitTask(taskSubmission);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

}