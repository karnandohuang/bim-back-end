package com.inventory.models;

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

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createddate", "updateddate", "createdby", "updatedby"},
        allowGetters = true
)
public abstract class BaseEntity implements Serializable {

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "createddate")
    @CreatedDate
    protected Date createdDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "updateddate")
    @LastModifiedDate
    protected Date updatedDate;

    @CreatedBy
    @Column(name = "createdby")
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "updatedby")
    protected String updatedBy;
}
