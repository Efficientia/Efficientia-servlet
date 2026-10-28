package com.efficientia.efficientia.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fábrica de conexões gerenciada por Pool de Alta Performance (HikariCP).
 *
 * Fornece conexões persistentes pré-aquecidas para o banco relacional PostgreSQL,
 * reduzindo a latência de negociação TCP/TLS com servidores em nuvem (Aiven)
 * de múltiplos segundos para menos de 5 milissegundos por requisição.
 */
public class ConnectionFactory {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static final Dotenv dotenv = loadDotenv();
    private static volatile HikariDataSource dataSource;

    private static Dotenv loadDotenv() {
        // 1. Tenta carregar se estiver rodando diretamente a partir da raiz
        File directEnv = new File(".env");
        if (directEnv.exists()) {
            return Dotenv.configure().load();
        }

        // 2. Tenta encontrar o .env na raiz do projeto subindo a árvore de pastas
        // a partir da pasta onde a classe compilada está (cobre Tomcat, WARs explodidos e IDEs)
        try {
            var codeSource = ConnectionFactory.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File current = new File(codeSource.getLocation().toURI());
                while (current != null) {
                    File candidate = new File(current, ".env");
                    if (candidate.exists()) {
                        return Dotenv.configure().directory(current.getAbsolutePath()).load();
                    }
                    current = current.getParentFile();
                }
            }
        } catch (Exception ignored) {
        }

        // 3. Fallback: carrega variáveis de ambiente do sistema sem disparar erro se o .env não for achado
        return Dotenv.configure().ignoreIfMissing().load();
    }

    /**
     * Inicializa o pool de conexões com parâmetros otimizados para banco em nuvem.
     */
    private static synchronized void initDataSource() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        String url = dotenv.get("db.url");
        String user = dotenv.get("db.user");
        String password = dotenv.get("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Configure db.url, db.user e db.password no .env local.");
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("EfficientiaHikariPool");
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // Otimização de parâmetros do Pool para banco remoto na nuvem (Aiven PostgreSQL)
        config.setMaximumPoolSize(10);        // Limite seguro para não esgotar conexões do plano de nuvem
        config.setMinimumIdle(2);             // Mantém conexões quentes e prontas para atendimento imediato
        config.setIdleTimeout(60000);         // 1 minuto: encerra conexões excedentes ociosas
        config.setConnectionTimeout(10000);   // 10 segundos: tempo limite para obter conexão
        config.setMaxLifetime(1800000);       // 30 minutos: renova conexões antes de expiração de firewall/cloud
        config.setKeepaliveTime(60000);       // 1 minuto: ping periódico preventivo contra timeout de NAT

        dataSource = new HikariDataSource(config);
    }

    /**
     * Obtém uma conexão pronta e ativa do pool HikariCP.
     *
     * @return Connection ativa proveniente do pool gerenciado
     * @throws SQLException caso ocorra falha de comunicação ou configuração de credenciais
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * Encerra o pool de conexões liberando todos os recursos de rede.
     */
    public static synchronized void fecharPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }

    /**
     * Verifica se o pool de conexões está ativo e operacional.
     */
    public static boolean isPoolAtivo() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Retorna a instância ativa do DataSource (útil para telemetria e testes).
     */
    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
