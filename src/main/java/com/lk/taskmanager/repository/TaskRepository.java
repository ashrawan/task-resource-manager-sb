package com.lk.taskmanager.repository;

import com.lk.taskmanager.entities.TaskEntity;
import com.lk.taskmanager.entities.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {

    @Query("select t from TaskEntity t WHERE ((t.startDate BETWEEN :dateFrom AND :dateTo) OR (t.endDate BETWEEN :dateFrom AND :dateTo)) AND t.assignedTo.id = :userId ")
    List<TaskEntity> searchUsersTaskByDateBetween(Pageable pageable, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("userId") Long userId);

}
