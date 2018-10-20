package com.inventory.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.HibernateException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@JsonRootName(value = "employee")
@Getter
@Setter
@Entity
@Table(name = "employees", schema = "public", catalog = "inventory")
@JsonPropertyOrder({ "id", "name", "superiorId", "dob", "email", "password", "position", "division", "createdDate",
        "updatedDate", "createdBy", "updatedBy" })
public class Employee extends BaseEntity{
    @Id
    @GenericGenerator(name = "employee_sequence", strategy = "com.inventory.models.generators.EmployeeIdGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "employee_sequence")
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "superiorId")
    private String superiorId;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "dob")
    private String dob;
    @Column(name = "position")
    private String position;
    @Column(name = "division")
    private String division;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Employee employee = (Employee) o;

        if (id != null ? !id.equals(employee.id) : employee.id != null) return false;
        if (name != null ? !name.equals(employee.name) : employee.name != null) return false;
        if (email != null ? !email.equals(employee.email) : employee.email != null) return false;
        if (password != null ? !password.equals(employee.password) : employee.password != null) return false;
        if (dob != null ? !dob.equals(employee.dob) : employee.dob != null) return false;
        if (position != null ? !position.equals(employee.position) : employee.position != null) return false;
        if (division != null ? !division.equals(employee.division) : employee.division != null) return false;
        if (superiorId != null ? !superiorId.equals(employee.superiorId) : employee.superiorId != null) return false;
        if (createdDate != null ? !createdDate.equals(employee.createdDate) : employee.createdDate != null)
            return false;
        if (updatedDate != null ? !updatedDate.equals(employee.updatedDate) : employee.updatedDate != null)
            return false;
        if (createdBy != null ? !createdBy.equals(employee.createdBy) : employee.createdBy != null) return false;
        if (updatedBy != null ? !updatedBy.equals(employee.updatedBy) : employee.updatedBy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (superiorId != null ? superiorId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (dob != null ? dob.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (division != null ? division.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        return result;
    }


}
