package com.inventory.models.generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeIdGenerator implements IdentifierGenerator {

    private final String DEFAULT_SEQUENCE_NAME = "employee_sequence";

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor
            , Object o) throws HibernateException {

        Serializable result = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        String prefix = "EM";


        try {
            connection = sharedSessionContractImplementor.connection();
            statement = connection.createStatement();
            try {
                statement.executeUpdate("UPDATE " + DEFAULT_SEQUENCE_NAME + " SET next_val = (next_val+1)");
                rs = statement.executeQuery("SELECT next_val FROM  " + DEFAULT_SEQUENCE_NAME);
            } catch (Exception e) {
                System.out.println("In catch, cause : Table is not available.");
                statement.execute("CREATE table " + DEFAULT_SEQUENCE_NAME + "(sequence_id SERIAL NOT NULL," +
                        "next_val INT NOT NULL)");
                statement.executeUpdate("INSERT INTO " + DEFAULT_SEQUENCE_NAME + " VALUES(0)");
                statement.executeUpdate("UPDATE " + DEFAULT_SEQUENCE_NAME + " SET next_val = (next_val+1)");
                rs = statement.executeQuery("SELECT next_val FROM  " + DEFAULT_SEQUENCE_NAME);
            }
            if (rs.next()) {
                int id = rs.getInt(1);
                String suffix = String.format("%03d", id);
                result = prefix + suffix;
                System.out.println("Generated Id: " + result);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}