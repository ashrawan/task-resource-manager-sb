package com.lk.taskmanager.services.domain.task;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.generic.GenericSpecification;
import com.lk.taskmanager.services.generic.SearchCriteria;
import com.lk.taskmanager.services.generic.SearchOperation;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.utils.Enums;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Optional;

public class TaskSearchSpecification {

    public static GenericSpecification<TaskEntity> processDynamicTaskFilter(GenericFilterRequestDTO<TaskEntity> genericFilterRequest) {
        GenericSpecification<TaskEntity> dynamicTaskSpec = new GenericSpecification<TaskEntity>();
        TaskEntity taskFilter = genericFilterRequest.getDataFilter();
        if (taskFilter.getId() != null) {
            dynamicTaskSpec.add(new SearchCriteria("id", taskFilter.getId(), SearchOperation.EQUAL));
        }
        if (taskFilter.getTitle() != null) {
            dynamicTaskSpec.add(new SearchCriteria("title", taskFilter.getTitle(), SearchOperation.MATCH));
        }
        if (taskFilter.getDescription() != null) {
            dynamicTaskSpec.add(new SearchCriteria("description", taskFilter.getDescription(), SearchOperation.MATCH));
        }
        if (taskFilter.getStatus() != null && Enums.TaskStatus.valueOf(taskFilter.getStatus().toString()) != null) {
            dynamicTaskSpec.add(new SearchCriteria("status", taskFilter.getStatus(), SearchOperation.EQUAL));
        }
        return dynamicTaskSpec;
    }

    public static Specification<TaskEntity> getTaskAssignedToUser(GenericFilterRequestDTO<TaskEntity> genericFilterRequest) {
        Specification<TaskEntity> taskEntitySpecification = new GenericSpecification<>();
        Long userId = Optional.ofNullable(genericFilterRequest)
                .map(GenericFilterRequestDTO<TaskEntity>::getDataFilter)
                .map(TaskEntity::getAssignedTo)
                .map(UserEntity::getId)
                .orElse(null);
        if (userId != null) {
            taskEntitySpecification = (root, query, criteriaBuilder) -> {
                Join<TaskEntity, UserEntity> userJoin = root.join("assignedTo");
                Predicate equalPredicate = criteriaBuilder.equal(userJoin.get("id"), userId);
                query.distinct(true);
                return equalPredicate;
            };
        }
        return taskEntitySpecification;
    }

    public static Specification<TaskEntity> getTaskReportToUser(GenericFilterRequestDTO<TaskEntity> genericFilterRequest) {
        Specification<TaskEntity> taskEntitySpecification = new GenericSpecification<>();
        Long userId = Optional.ofNullable(genericFilterRequest)
                .map(GenericFilterRequestDTO<TaskEntity>::getDataFilter)
                .map(TaskEntity::getReportTo)
                .map(UserEntity::getId)
                .orElse(null);
        if (userId != null) {
            taskEntitySpecification = (root, query, criteriaBuilder) -> {
                Join<TaskEntity, UserEntity> userJoin = root.join("reportTo");
                Predicate equalPredicate = criteriaBuilder.equal(userJoin.get("id"), userId);
                query.distinct(true);
                return equalPredicate;
            };
        }
        return taskEntitySpecification;
    }

}
