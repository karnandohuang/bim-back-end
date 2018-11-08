package com.inventory.models;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.inventory.models.Constant.*;

@Data
@Entity
@Table(name = ADMIN_TABLE_NAME)
public class Admin extends BaseEntity {

    @Id
    @GenericGenerator(name = "admin_sequence", strategy = "com.inventory.models.generators.AdminIdGenerator")
    @GeneratedValue(generator = "admin_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;

    @Column(name = ADMIN_COLUMN_NAME_EMAIL)
    private String email;

    @Column(name = ADMIN_COLUMN_NAME_PASSWORD)
    private String password;
}
