package com.lk.taskmanager.entities;

import com.lk.taskmanager.utils.Enums;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.RawDataStatus status;
}
