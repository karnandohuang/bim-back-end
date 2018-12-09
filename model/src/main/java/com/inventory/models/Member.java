package com.inventory.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import static com.inventory.models.Constant.MEMBER_COLUMN_NAME_EMAIL;
import static com.inventory.models.Constant.MEMBER_COLUMN_NAME_PASSWORD;

@Data
@MappedSuperclass
public abstract class Member extends BaseEntity {
    @Column(name = MEMBER_COLUMN_NAME_EMAIL)
    protected String email;

    @Column(name = MEMBER_COLUMN_NAME_PASSWORD)
    protected String password;
}
