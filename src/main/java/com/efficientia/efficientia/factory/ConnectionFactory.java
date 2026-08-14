package com.efficientia.efficientia.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public static Connection getConnection() throws SQLException{

        return DriverManager.getConnection(
                "jdbc:postgresql://pg-2eb87185-institutojef-9d35.h.aivencloud.com:17645/dbEfficientia",
                "avnadmin",
                "AVNS_YwdNrYkPZeT6h_ab4U3"

        );
    }
}
