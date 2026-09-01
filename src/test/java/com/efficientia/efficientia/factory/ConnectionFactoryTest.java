package com.efficientia.efficientia.factory;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConnectionFactoryTest {

    @Test
    public void deveConectarAoBanco() throws Exception {

        Connection connection = ConnectionFactory.getConnection();

        assertNotNull(connection);

        connection.close();
    }
}