package com.lk.taskmanager.services.domain.user;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.generic.GenericSpecification;
import com.lk.taskmanager.services.generic.SearchCriteria;
import com.lk.taskmanager.services.generic.SearchOperation;
import com.lk.taskmanager.services.generic.dtos.GenericFilterRequestDTO;
import com.lk.taskmanager.utils.Enums;

public class UserSearchSpecification {

    public static GenericSpecification<UserEntity> processDynamicUserFilter(GenericFilterRequestDTO<UserEntity> genericFilterRequest) {
        GenericSpecification<UserEntity> dynamicUserSpec = new GenericSpecification<UserEntity>();
        UserEntity userFilter = genericFilterRequest.getDataFilter();
        if (userFilter.getId() != null) {
            dynamicUserSpec.add(new SearchCriteria("id", userFilter.getId(), SearchOperation.EQUAL));
        }
        if (userFilter.getUsername() != null) {
            dynamicUserSpec.add(new SearchCriteria("username", userFilter.getUsername(), SearchOperation.MATCH));
        }
        if (userFilter.getRole() != null) {
            dynamicUserSpec.add(new SearchCriteria("role", userFilter.getRole(), SearchOperation.EQUAL));
        }
        if (userFilter.getFullName() != null) {
            dynamicUserSpec.add(new SearchCriteria("fullName", userFilter.getFullName(), SearchOperation.MATCH));
        }
        if (userFilter.getPhoneNumber() != null) {
            dynamicUserSpec.add(new SearchCriteria("phoneNumber", userFilter.getPhoneNumber(), SearchOperation.MATCH));
        }
        if (userFilter.getStatus() != null && Enums.UserStatus.valueOf(userFilter.getStatus().toString()) != null) {
            dynamicUserSpec.add(new SearchCriteria("status", userFilter.getStatus(), SearchOperation.EQUAL));
        }
        return dynamicUserSpec;
    }
}
