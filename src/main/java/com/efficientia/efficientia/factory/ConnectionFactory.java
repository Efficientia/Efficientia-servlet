package com.efficientia.efficientia.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionFactory {

    public static Connection getConnection() throws SQLException{

        Dotenv dotenv = Dotenv.load();

        return DriverManager.getConnection(
                dotenv.get("db.url"),
                dotenv.get("db.user"),
                dotenv.get("db.password")

        );
    }
}
