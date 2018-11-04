package com.inventory.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.Collection;

import static com.inventory.models.Constant.*;

@JsonRootName(value = "employee")
@Data
@Entity
@Table(name = EMPLOYEE_TABLE_NAME, schema = SCHEMA_NAME, catalog = DATABASE_NAME)
@JsonPropertyOrder({"id", "name", "superiorId", "dob", "email", "password", "position", "division", "createdDate",
        "updatedDate", "createdBy", "updatedBy"})
public class Employee extends BaseEntity{
    @Id
    @GenericGenerator(name = "employee_sequence", strategy = "com.inventory.models.generators.EmployeeIdGenerator")
    @GeneratedValue(generator = "employee_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;
    @Column(name = EMPLOYEE_COLUMN_NAME_NAME)
    private String name;
    @Column(name = EMPLOYEE_COLUMN_NAME_SUPERIOR_ID)
    private String superiorId;
    @Column(name = EMPLOYEE_COLUMN_NAME_EMAIL)
    private String email;
    @Column(name = EMPLOYEE_COLUMN_NAME_PASSWORD)
    private String password;
    @Column(name = EMPLOYEE_COLUMN_NAME_DOB)
    private String dob;
    @Column(name = EMPLOYEE_COLUMN_NAME_POSITION)
    private String position;
    @Column(name = EMPLOYEE_COLUMN_NAME_DIVISION)
    private String division;
}
