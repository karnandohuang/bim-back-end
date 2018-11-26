package com.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.inventory.models.Constant.*;

@Data
@Entity
@Table(name = REQUEST_TABLE_NAME, schema = SCHEMA_NAME, catalog = DATABASE_NAME)
@JsonPropertyOrder({"id", "employeeId", "itemId", "qty", "status", "notes", "createdDate",
        "updatedDate", "createdBy", "updatedBy"})
public class Request extends BaseEntity {

    @Id
    @GenericGenerator(name = "request_sequence", strategy = "com.inventory.models.generators.RequestIdGenerator")
    @GeneratedValue(generator = "request_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;

    @Column(name = REQUEST_COLUMN_NAME_EMPLOYEE_ID)
    private String employeeId;

    @Column(name = REQUEST_COLUMN_NAME_ITEM_ID)
    private String itemId;

    @Column(name = REQUEST_COLUMN_NAME_QTY)
    private Integer qty;

    @Column(name = REQUEST_COLUMN_NAME_STATUS)
    private String status;

    @Column(name = REQUEST_COLUMN_NAME_NOTES)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEEID")
    @JsonIgnore
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "ITEMID")
    @JsonIgnore
    private Item item;

}
