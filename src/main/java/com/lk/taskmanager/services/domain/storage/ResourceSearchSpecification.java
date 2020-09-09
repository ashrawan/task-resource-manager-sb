package com.lk.taskmanager.services.domain.storage;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.generic.GenericSpecification;
import com.lk.taskmanager.services.generic.SearchCriteria;
import com.lk.taskmanager.services.generic.SearchOperation;
import com.lk.taskmanager.utils.Enums;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class ResourceSearchSpecification {

    protected static GenericSpecification<ResourceInfoEntity> processDynamicResourceInfoFilter(ResourceInfoEntity resourceInfoEntity) {
        GenericSpecification<ResourceInfoEntity> dynamicResourceInfoSpec = new GenericSpecification<ResourceInfoEntity>();
        ResourceInfoEntity resourceInfoFilter = resourceInfoEntity;
        if (resourceInfoFilter.getId() != null) {
            dynamicResourceInfoSpec.add(new SearchCriteria("id", resourceInfoFilter.getId(), SearchOperation.EQUAL));
        }
        if (resourceInfoFilter.getResourceName() != null) {
            dynamicResourceInfoSpec.add(new SearchCriteria("resourceName", resourceInfoFilter.getResourceName(), SearchOperation.MATCH));
        }
        if (resourceInfoFilter.getResourceSize() != null) {
            dynamicResourceInfoSpec.add(new SearchCriteria("resourceSize", resourceInfoFilter.getResourceSize(), SearchOperation.GREATER_THAN_EQUAL));
        }
        if (resourceInfoFilter.getResourceType() != null) {
            dynamicResourceInfoSpec.add(new SearchCriteria("resourceType", resourceInfoFilter.getResourceType(), SearchOperation.MATCH));
        }
        if (resourceInfoFilter.getStatus() != null && Enums.RawDataStatus.valueOf(resourceInfoFilter.getStatus().toString()) != null) {
            dynamicResourceInfoSpec.add(new SearchCriteria("status", resourceInfoFilter.getStatus(), SearchOperation.EQUAL));
        }

        return dynamicResourceInfoSpec;
    }

    public static Specification<ResourceInfoEntity> getByUserHasAccessToResources(Long userId, Long resourceId) {
        Specification<ResourceInfoEntity> resourceInfoEntitySpecification = new GenericSpecification<>();
        if (userId != null) {
            resourceInfoEntitySpecification = (root, query, criteriaBuilder) -> {

                Join<ResourceInfoEntity, UserEntity> userJoin = root.join("userAccessList");
                criteriaBuilder.equal(root.get("id"), resourceId);
                Predicate equalPredicate = criteriaBuilder.equal(userJoin.get("id"), userId);
                query.distinct(true);
                return equalPredicate;
            };
        }
        return resourceInfoEntitySpecification;
    }
}
