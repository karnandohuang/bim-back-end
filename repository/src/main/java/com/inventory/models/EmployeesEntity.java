package com.inventory.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "employees", schema = "public", catalog = "inventory")
public class EmployeesEntity {
    private String id;
    private String superiorid;
    private String name;
    private String email;
    private String password;
    private String dob;
    private String position;
    private String division;
    private String createddate;
    private String updateddate;
    private String createdby;
    private String updatedby;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "superiorid")
    public String getSuperiorid() {
        return superiorid;
    }

    public void setSuperiorid(String superiorid) {
        this.superiorid = superiorid;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "dob")
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Basic
    @Column(name = "position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Basic
    @Column(name = "division")
    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    @Basic
    @Column(name = "createddate")
    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    @Basic
    @Column(name = "updateddate")
    public String getUpdateddate() {
        return updateddate;
    }

    public void setUpdateddate(String updateddate) {
        this.updateddate = updateddate;
    }

    @Basic
    @Column(name = "createdby")
    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    @Basic
    @Column(name = "updatedby")
    public String getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(String updatedby) {
        this.updatedby = updatedby;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeesEntity that = (EmployeesEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(superiorid, that.superiorid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(dob, that.dob) &&
                Objects.equals(position, that.position) &&
                Objects.equals(division, that.division) &&
                Objects.equals(createddate, that.createddate) &&
                Objects.equals(updateddate, that.updateddate) &&
                Objects.equals(createdby, that.createdby) &&
                Objects.equals(updatedby, that.updatedby);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, superiorid, name, email, password, dob, position, division, createddate, updateddate, createdby, updatedby);
    }
}
