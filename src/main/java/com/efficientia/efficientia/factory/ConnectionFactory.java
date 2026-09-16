package com.efficientia.efficientia.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionFactory {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL não encontrado: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException{

        Dotenv dotenv = Dotenv.load();

        return DriverManager.getConnection(
                dotenv.get("db.url"),
                dotenv.get("db.user"),
                dotenv.get("db.password")

        );
    }
}
