package com.inventory.models.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inventory.models.abstract_entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.inventory.models.Constant.*;

@Data
@Entity
@Table(name = ASSIGNMENT_TABLE_NAME, schema = SCHEMA_NAME, catalog = DATABASE_NAME)
@JsonPropertyOrder({"id", "employeeId", "itemId", "qty", "status", "notes", "createdDate",
        "updatedDate", "createdBy", "updatedBy"})
public class Assignment extends BaseEntity {

    @Id
    @GenericGenerator(name = "assignment_sequence", strategy = "com.inventory.models.generators.AssignmentIdGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "assignment_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;

    @Column(name = ASSIGNMENT_COLUMN_NAME_QTY)
    private Integer qty;

    @Column(name = ASSIGNMENT_COLUMN_NAME_STATUS)
    private String status;

    @Column(name = ASSIGNMENT_COLUMN_NAME_NOTES)
    private String notes;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "employee_pkey_fk"), name = ASSIGNMENT_COLUMN_NAME_EMPLOYEE_ID,
            referencedColumnName = COLUMN_NAME_ID)
    private Employee employee;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "item_pkey_fk"), name = ASSIGNMENT_COLUMN_NAME_ITEM_ID,
            referencedColumnName = COLUMN_NAME_ID)
    private Item item;

}
