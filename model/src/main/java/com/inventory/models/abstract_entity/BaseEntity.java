package com.inventory.models.abstract_entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static com.inventory.models.Constant.*;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createddate", "updateddate", "createdby", "updatedby"},
        allowGetters = true
)
public abstract class BaseEntity implements Serializable {

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = COLUMN_NAME_CREATED_DATE, updatable = false)
    @CreatedDate
    protected Date createdDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = COLUMN_NAME_UPDATED_DATE)
    @LastModifiedDate
    protected Date updatedDate;

    @CreatedBy
    @Column(name = COLUMN_NAME_CREATED_BY, updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(name = COLUMN_NAME_UPDATED_BY)
    protected String updatedBy;
}
