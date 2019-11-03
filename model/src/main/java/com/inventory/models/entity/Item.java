package com.inventory.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.inventory.models.abstract_entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

import static com.inventory.models.Constant.*;


@JsonRootName(value = "value")
@Data
@Entity
@Table(name = ITEM_TABLE_NAME, schema = SCHEMA_NAME, catalog = DATABASE_NAME)
@JsonPropertyOrder({"id", "sku", "name", "price", "location", "imageurl", "createdDate",
        "updatedDate", "createdBy", "updatedBy"})
public class Item extends BaseEntity {
    @Id
    @GenericGenerator(name = "item_sequence", strategy = "com.inventory.models.generators.ItemIdGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "item_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;
    @Column(name = ITEM_COLUMN_NAME_NAME)
    private String name;
    @Column(name = ITEM_COLUMN_NAME_PRICE)
    private int price;
    @Column(name = ITEM_COLUMN_NAME_QTY)
    private int qty;
    @Column(name = ITEM_COLUMN_NAME_LOCATION)
    private String location;
    @Column(name = ITEM_COLUMN_NAME_IMAGE_URL)
    private String imageUrl;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Assignment> assignmentList;
}
