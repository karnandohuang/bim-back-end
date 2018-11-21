package com.inventory.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.inventory.models.Constant.*;


@JsonRootName(value = "item")
@Data
@Entity
@Table(name=ITEM_TABLE_NAME, schema = SCHEMA_NAME, catalog = DATABASE_NAME)
@JsonPropertyOrder({"id", "sku", "name", "price", "location", "imageurl", "createdDate",
        "updatedDate", "createdBy", "updatedBy"})
public class Item extends BaseEntity {
    @Id
    @GenericGenerator(name = "item_sequence", strategy = "com.inventory.models.generators.ItemIdGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "item_sequence")
    @Column(name = COLUMN_NAME_ID)
    private String id;
    @Column(name = ITEM_COLUMN_NAME_SKU)
    private String sku;
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

}
