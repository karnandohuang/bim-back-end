package com.inventory.models.entity;

import com.inventory.models.abstract_entity.Member;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.inventory.models.Constant.*;

@Data
@Entity
@Table(name = ADMIN_TABLE_NAME, schema = SCHEMA_NAME, catalog = DATABASE_NAME)
public class Admin extends Member {

    @Id
    @GenericGenerator(name = "admin_sequence", strategy = "com.inventory.models.generators.AdminIdGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "admin_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;
}