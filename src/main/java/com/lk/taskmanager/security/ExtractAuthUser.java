package com.lk.taskmanager.security;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.generic.StringToEnumConverter;
import com.lk.taskmanager.utils.Enums;
import com.lk.taskmanager.utils.exceptions.AppExceptionConstants;
import com.lk.taskmanager.utils.exceptions.UnAuthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ExtractAuthUser {

    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }

    public static boolean isOwner(Long userId) {
        UserEntity userEntity = getCurrentUser();
        if (userEntity.getId() == userId) {
            return true;
        }
        return false;
    }

    public static boolean isAdmin() {
        UserEntity userEntity = getCurrentUser();
        boolean isAdmin = userEntity.getRole().equals(String.valueOf(Enums.UserRoleStatus.ROLE_ADMIN));
        return isAdmin;
    }

    public static Long resolveUserId(Long userId) {
        if (userId == null) {
            userId = ExtractAuthUser.getCurrentUser().getId();
        }
        return userId;
    }

    public static boolean hasAdminAccessOrIsOwner(Long resourceOwnerId) {
        if (resourceOwnerId == null) {
            return false;
        }
        if (isAdmin() || isOwner(resourceOwnerId)) {
            return true;
        }
        return false;
    }

}
