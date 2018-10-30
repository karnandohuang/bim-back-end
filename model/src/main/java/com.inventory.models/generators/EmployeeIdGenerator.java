package com.inventory.models.generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.*;

public class EmployeeIdGenerator implements IdentifierGenerator {

    private final String DEFAULT_SEQUENCE_NAME = "employee_sequence";
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/inventory";

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor
            , Object o) throws HibernateException {

        Serializable result = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        String prefix = "EM";

        try {
            connection = DriverManager.getConnection(DATABASE_URL,"postgres","power7500");
            statement = connection.createStatement();
            try {
                rs = statement.executeQuery("SELECT  NEXTVAL('" + DEFAULT_SEQUENCE_NAME + "')");
            }catch(Exception e) {
                statement = connection.createStatement();
                System.out.println("In catch, cause : Table is not available.");
                statement.execute("CREATE SEQUENCE " + DEFAULT_SEQUENCE_NAME + " START 1");
                rs = statement.executeQuery("SELECT  NEXTVAL('" + DEFAULT_SEQUENCE_NAME + "')");
            }
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
}