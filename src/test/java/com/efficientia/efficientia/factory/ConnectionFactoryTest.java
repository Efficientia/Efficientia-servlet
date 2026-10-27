package com.efficientia.efficientia.factory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionFactoryTest {

    @AfterAll
    public static void tearDown() {
        ConnectionFactory.fecharPool();
    }

    @Test
    @DisplayName("Deve obter conexão válida e ativar o pool HikariCP")
    public void deveConectarAoBanco() throws Exception {
        try (Connection connection = ConnectionFactory.getConnection()) {
            assertNotNull(connection, "A conexão não deve ser nula.");
            assertFalse(connection.isClosed(), "A conexão deve estar aberta.");
            assertTrue(ConnectionFactory.isPoolAtivo(), "O pool HikariCP deve estar ativo.");
        }
    }

    @Test
    @DisplayName("Deve reutilizar conexões do pool com latência inferior a 100 ms")
    public void deveReutilizarConexaoComBaixaLatencia() throws Exception {
        // Garante aquecimento do pool
        try (Connection warmup = ConnectionFactory.getConnection()) {
            assertNotNull(warmup);
        }

        // Mede tempo de obtenção de conexão reaproveitada
        long inicio = System.currentTimeMillis();
        try (Connection conn = ConnectionFactory.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
        long tempoTotal = System.currentTimeMillis() - inicio;

        System.out.println("Tempo para obter conexão reutilizada do pool: " + tempoTotal + " ms");
        assertTrue(tempoTotal < 100, "Conexão reutilizada do pool deve levar menos de 100 ms (levou " + tempoTotal + " ms)");
    }

    @Test
    @DisplayName("Deve fechar e permitir reiniciar o pool com êxito")
    public void deveFecharEReiniciarPoolComSucesso() throws Exception {
        assertTrue(ConnectionFactory.isPoolAtivo());

        // Encerra o pool
        ConnectionFactory.fecharPool();
        assertFalse(ConnectionFactory.isPoolAtivo());

        // Reabre automaticamente na próxima chamada
        try (Connection conn = ConnectionFactory.getConnection()) {
            assertNotNull(conn);
            assertTrue(ConnectionFactory.isPoolAtivo());
        }
    }
}