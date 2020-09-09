package com.lk.taskmanager.services.domain.storage;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceInfoService {

    GenericResponseDTO<List<ResourceInfoEntity>> getAllResourceByOwnerId(Pageable pageable, Long resourceOwnerId);

    GenericResponseDTO<ResourceInfoEntity> getResourceById(Long resourceId);

    GenericResponseDTO<ResourceInfoEntity> storeResource(MultipartFile file);

    Resource downloadResourceByResourceId(Long resourceId);

    GenericResponseDTO<List<ResourceInfoEntity>> filterResourceInfoData(GenericFilterRequestDTO<ResourceInfoEntity> genericFilterRequestDTO, Pageable pageable);

    GenericResponseDTO<?> deleteResourceById(Long id);
}
