package com.poly.livre.backend.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode
public abstract class AuditDateEntity {

    @CreatedDate
    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private Instant updatedAt;

}