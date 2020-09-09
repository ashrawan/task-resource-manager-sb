package com.lk.taskmanager.services.domain.storage;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceInfoService {

    List<ResourceInfoEntity> getAllResourceByOwnerId(Pageable pageable, Long resourceOwnerId);

    ResourceInfoEntity getResourceById(Long resourceId);

    GenericResponseDTO<?> storeResource(MultipartFile file);

    Resource loadResourceByResourceNameAndOwnerId(String resourceName, Long resourceOwnerId);

    GenericResponseDTO<?> deleteResourceById(Long id);
}
