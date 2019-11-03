package com.inventory.models.generators;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.sql.*;

import static com.inventory.models.Constant.*;

public class ItemIdGenerator extends EntityIdGenerator {

    private static final String DEFAULT_SEQUENCE_NAME = "item_sequence";

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor
            , Object o) {

        Serializable result = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        String prefix = "IM";


        try {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSCODE);
            statement = connection.createStatement();
            rs = getCurrentSequenceValue(connection, statement);
            if (rs.next()) {
                int id = rs.getInt(1);
                String suffix = String.format("%03d", id);
                result = prefix + suffix;
                System.out.println("Generated Id: " + result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected ResultSet getCurrentSequenceValue(Connection connection, Statement statement) throws SQLException {
        ResultSet rs;
        try {
            rs = statement.executeQuery("SELECT NEXTVAL('" + DEFAULT_SEQUENCE_NAME + "')");
        } catch (Exception e) {
            statement = connection.createStatement();
            System.out.println("In catch, cause : Table is not available.");
            statement.execute("CREATE SEQUENCE " + DEFAULT_SEQUENCE_NAME + " START 1");

            rs = statement.executeQuery("SELECT NEXTVAL('" + DEFAULT_SEQUENCE_NAME + "')");
        }
        return rs;
    }
}
