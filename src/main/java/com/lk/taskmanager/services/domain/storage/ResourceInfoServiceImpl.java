package com.lk.taskmanager.services.domain.storage;

import com.lk.taskmanager.configuration.StorageConfig;
import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.ResourceInfoRepository;
import com.lk.taskmanager.repository.TaskRepository;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.generic.GenericSpecification;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.Enums;
import com.lk.taskmanager.utils.MessageCodeUtil;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.LKAppException;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import com.lk.taskmanager.utils.exceptions.UnAuthorizedAccessException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ResourceInfoServiceImpl implements ResourceInfoService {

    private final StorageConfig storageConfig;
    private final ResourceInfoRepository resourceInfoRepository;
    private final TaskRepository taskRepository;

    @Override
    public GenericResponseDTO<List<ResourceInfoEntity>> getAllResourceByOwnerId(Pageable pageable, Long resourceOwnerId) {
        List<ResourceInfoEntity> resourceInfoList = resourceInfoRepository.findAllByResourceOwnerId(pageable, resourceOwnerId);
        return ResponseBuilder.buildSuccessResponse(resourceInfoList, resourceInfoList.size());
    }

    public GenericResponseDTO<ResourceInfoEntity> getResourceById(Long resourceId) {
        ResourceInfoEntity resourceInfo = resourceInfoRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND));
        return ResponseBuilder.buildSuccessResponse(resourceInfo);
    }

    public GenericResponseDTO<ResourceInfoEntity> storeResource(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = System.currentTimeMillis() + "-" + fileName;
        try {
            if (fileName.contains("..")) {
                return ResponseBuilder.buildFailureResponse(MessageCodeUtil.FILENAME_ERROR);
            }
            ResourceInfoEntity rawDataEntity = createRawDataInfo(fileName, file.getSize(), file.getContentType(), Enums.RawDataStatus.ACTIVE);

            String username = ExtractAuthUser.getCurrentUser().getUsername();
            Path userDirectory = storageConfig.getUserDirectory(username);
            Path targetLocation = userDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            resourceInfoRepository.save(rawDataEntity);
            return ResponseBuilder.buildSuccessResponse(rawDataEntity);

        } catch (IOException ex) {
            return ResponseBuilder.buildFailureResponse(MessageCodeUtil.FILESTORAGE_ERROR);
        }

    }

    private GenericResponseDTO<ResourceInfoEntity> getResourceByIdIfUserInAccessList(Long resourceId) {
        ResourceInfoEntity resourceInfoEntity = new ResourceInfoEntity();
        resourceInfoEntity.setId(resourceId);
        Long currentUserId = ExtractAuthUser.getCurrentUser().getId();
        Specification<ResourceInfoEntity> specUserHasAccessToResources = ResourceSearchSpecification.getByUserHasAccessToResources(currentUserId, resourceId);
        List<ResourceInfoEntity> resourceInfoEntities = resourceInfoRepository.findAll(Specification.where(specUserHasAccessToResources));
        if (resourceInfoEntities.size() < 1) {
            throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
        }
        return ResponseBuilder.buildSuccessResponse(resourceInfoEntities.get(0));
    }

    public Resource downloadResourceByResourceId(Long resourceId) {
        try {
            ResourceInfoEntity resourceInfo = resourceInfoRepository.findById(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND));
            if (!ExtractAuthUser.isAdmin()) {
                if (!ExtractAuthUser.isOwner(resourceInfo.getResourceOwner().getId())) {
                    UserEntity userEntity = new UserEntity(ExtractAuthUser.getCurrentUser());
                    getResourceByIdIfUserInAccessList(resourceId);
                }
            }
            UserEntity resourceOwner = resourceInfo.getResourceOwner();
            Path filePath = storageConfig.getUserDirectory(resourceOwner.getUsername()).resolve(resourceInfo.getResourceName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND);
            }
        } catch (Exception ex) {
            throw new LKAppException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND, ex);
        }
    }

    public GenericResponseDTO<List<ResourceInfoEntity>> filterResourceInfoData(GenericFilterRequestDTO<ResourceInfoEntity> genericFilterRequest, Pageable pageable) {
        GenericSpecification<ResourceInfoEntity> resourceInfoSpec = ResourceSearchSpecification.processDynamicResourceInfoFilter(genericFilterRequest.getDataFilter());
        Page<ResourceInfoEntity> filteredResourcesInfo = resourceInfoRepository.findAll(resourceInfoSpec, pageable);
        GenericResponseDTO<List<ResourceInfoEntity>> genericResponse = ResponseBuilder.buildSuccessResponse(filteredResourcesInfo.getContent(), filteredResourcesInfo.getTotalElements());
        return genericResponse;
    }

    public GenericResponseDTO<?> deleteResourceById(Long id) {
        ResourceInfoEntity resourceInfo = resourceInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND));
        try {
            UserEntity resourceOwner = resourceInfo.getResourceOwner();
            ExtractAuthUser.isOwner(resourceOwner.getId());
            Path targetLocation = storageConfig.getUserDirectory(resourceOwner.getUsername()).resolve(resourceInfo.getResourceName());
            resourceInfoRepository.delete(resourceInfo);
            Files.delete(targetLocation);
        } catch (NoSuchFileException e) {
            throw new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND);
        } catch (Exception e) {
            ResponseBuilder.buildFailureResponse(MessageCodeUtil.UNKNOWN_ERROR);
        }
        return ResponseBuilder.buildSuccessResponse(null);
    }

    private ResourceInfoEntity createRawDataInfo(String dataName, Long dataSize, String dataType, Enums.RawDataStatus rawDataStatus) {
        ResourceInfoEntity resourceInfo = new ResourceInfoEntity();
        resourceInfo.setResourceName(dataName);
        resourceInfo.setResourceSize(dataSize);
        resourceInfo.setResourceType(dataType);
        resourceInfo.setStatus(rawDataStatus);
        resourceInfo.setResourceOwner(ExtractAuthUser.getCurrentUser());
        return resourceInfo;
    }

}
