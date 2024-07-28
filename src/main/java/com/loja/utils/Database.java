package com.loja.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/db_projeto";
    private static final String USER = "postgres"; // Nome do Usuario do Banco
    private static final String PASSWORD = "KxjV4.Y_1";  //"springstudent" //"KxjV4.Y_1"

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
