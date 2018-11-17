package com.inventory.models;

public class Constant {

    public static final String EMPLOYEE_TABLE_NAME = "employees";
    public static final String REQUEST_TABLE_NAME = "requests";
    public static final String ITEM_TABLE_NAME = "items";
    public static final String ADMIN_TABLE_NAME = "admins";

    public static final String DATABASE_NAME = "inventory";
    public static final String SCHEMA_NAME = "public";

    public static final String ADMIN_COLUMN_NAME_EMAIL = "email";
    public static final String ADMIN_COLUMN_NAME_PASSWORD = "password";

    public static final String COLUMN_NAME_ID = "id";
    public static final String EMPLOYEE_COLUMN_NAME_SUPERIOR_ID = "superiorId";
    public static final String EMPLOYEE_COLUMN_NAME_NAME = "name";
    public static final String EMPLOYEE_COLUMN_NAME_EMAIL = "email";
    public static final String EMPLOYEE_COLUMN_NAME_PASSWORD = "password";
    public static final String EMPLOYEE_COLUMN_NAME_DOB = "dob";
    public static final String EMPLOYEE_COLUMN_NAME_POSITION = "position";
    public static final String EMPLOYEE_COLUMN_NAME_DIVISION = "division";
    public static final String EMPLOYEE_COLUMN_NAME_ROLE = "role";

    public static final String ITEM_COLUMN_NAME_SKU = "sku";
    public static final String ITEM_COLUMN_NAME_NAME = "name";
    public static final String ITEM_COLUMN_NAME_PRICE = "price";
    public static final String ITEM_COLUMN_NAME_QTY = "qty";
    public static final String ITEM_COLUMN_NAME_LOCATION = "location";
    public static final String ITEM_COLUMN_NAME_IMAGE_URL = "imageurl";


    public static final String REQUEST_COLUMN_NAME_EMPLOYEE_ID = "employeeId";
    public static final String REQUEST_COLUMN_NAME_ITEM_ID = "itemId";
    public static final String REQUEST_COLUMN_NAME_QTY = "qty";
    public static final String REQUEST_COLUMN_NAME_STATUS = "status";
    public static final String REQUEST_COLUMN_NAME_NOTES = "notes";

    public static final String COLUMN_NAME_CREATED_DATE = "createddate";
    public static final String COLUMN_NAME_UPDATED_DATE = "updateddate";
    public static final String COLUMN_NAME_CREATED_BY = "createdby";
    public static final String COLUMN_NAME_UPDATED_BY = "updatedby";


}
