package com.lk.taskmanager.security;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.utils.Enums;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.UnAuthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ExtractAuthUser {

    public static UserEntity getCurrentUser(){
        return getCurrentUserEntity();
    }

    public static boolean isOwner(Long userId){
        UserEntity userEntity = getCurrentUserEntity();
        return userEntity.getId() == userId;
    }

    public static Long resolveUserId(Long userId) {
        if(userId == null) {
            userId = ExtractAuthUser.getCurrentUser().getId();
        }
        return userId;
    }

    public static Long resolveHasAdminAccessOrIsOwner(Long resourceOwnerId) {
        if(resourceOwnerId == null){
            resourceOwnerId = ExtractAuthUser.getCurrentUser().getId();
        }
        UserEntity userEntity = getCurrentUserEntity();
        boolean isAdmin = userEntity.getRole().equals(String.valueOf(Enums.UserRoleStatus.ADMIN));
        boolean ownResources = isOwner(resourceOwnerId);
        if(!isAdmin && !ownResources) {
            throw new UnAuthorizedAccessException(AppExceptionConstants.UNAUTHORIZED_ACCESS);
        }
        return resourceOwnerId;
    }

    private static UserEntity getCurrentUserEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }

}