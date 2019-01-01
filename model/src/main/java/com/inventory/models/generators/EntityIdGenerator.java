package com.inventory.models.generators;

import org.hibernate.id.IdentifierGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class EntityIdGenerator implements IdentifierGenerator {
    protected abstract ResultSet getCurrentSequenceValue(Connection connection, Statement statement) throws SQLException;
}
