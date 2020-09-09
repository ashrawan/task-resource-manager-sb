package com.lk.taskmanager.services.domain.storage;

import com.lk.taskmanager.configuration.StorageConfig;
import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.repository.ResourceInfoRepository;
import com.lk.taskmanager.repository.UserRepository;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import com.lk.taskmanager.services.generic.MessageCodeUtil;
import com.lk.taskmanager.services.generic.ResponseBuilder;
import com.lk.taskmanager.utils.Enums;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.LKAppException;
import com.lk.taskmanager.utils.exceptions.ResourceNotFoundException;
import com.lk.taskmanager.utils.exceptions.UnAuthorizedAccessException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;

    @Override
    public List<ResourceInfoEntity> getAllResourceByOwnerId(Pageable pageable, Long resourceOwnerId) {
        List<ResourceInfoEntity> resourceInfoList = resourceInfoRepository.findAllByResourceOwnerId(pageable, resourceOwnerId);
        return resourceInfoList;
    }

    public ResourceInfoEntity getResourceById(Long resourceId) {
        ResourceInfoEntity resourceInfo = resourceInfoRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND));
        return resourceInfo;
    }

//    public ResourceInfoEntity filterResource(String fileName) {
//        return null;
//    }

    public GenericResponseDTO<?> storeResource(MultipartFile file) {
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

    public Resource loadResourceByResourceNameAndOwnerId(String fileName, Long resourceOwnerId) {
        try {
            UserEntity userEntity = userRepository.findById(resourceOwnerId)
                    .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
            Path filePath = storageConfig.getUserDirectory(userEntity.getUsername()).resolve(fileName).normalize();
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

    public GenericResponseDTO<?> deleteResourceById(Long id) {
        ResourceInfoEntity resourceInfo = resourceInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.REQUESTED_RESOURCE_NOT_FOUND));
            try {
                UserEntity resourceOwner = resourceInfo.getResourceOwner();
                if(!ExtractAuthUser.isOwner(resourceOwner.getId())) {
                    throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
                }
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
        return resourceInfo;
    }

}
