package com.lk.taskmanager.repository;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceInfoRepository extends JpaRepository<ResourceInfoEntity, Long> {

    List<ResourceInfoEntity> findAllByResourceOwnerId(Pageable pageable, Long ownerId);
}
