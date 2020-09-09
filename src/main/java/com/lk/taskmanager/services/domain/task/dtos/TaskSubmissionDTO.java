package com.lk.taskmanager.services.domain.task.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TaskSubmissionDTO {

    private List<MultipartFile> multipartFiles;

    private Long userId;

    private Long taskId;

    private String subDescription;
}
