package com.lk.taskmanager.entities;

import com.lk.taskmanager.utils.Enums;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.scheduling.config.Task;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resources_info")
@Data
public class ResourceInfoEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "resource_size", nullable = false)
    private Long resourceSize;

    @Column(name = "resource_type")
    private String resourceType;

    @ManyToOne
    @JoinColumn(name = "resource_owner", nullable = false)
    private UserEntity resourceOwner;

    @ManyToMany
    @JoinTable(
            name = "resource_access_info",
            joinColumns = {@JoinColumn(name = "resource_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<UserEntity> userAccessList = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.RawDataStatus status;
}
