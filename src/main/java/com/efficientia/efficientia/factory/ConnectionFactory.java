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

        // 2. Suporte nativo a Secret Files em provedores Cloud (ex: Render monta em /etc/secrets/.env)
        File renderSecretEnv = new File("/etc/secrets/.env");
        if (renderSecretEnv.exists()) {
            return Dotenv.configure().directory("/etc/secrets").load();
        }

        // 3. Tenta encontrar o .env na raiz do projeto subindo a árvore de pastas
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

        // 4. Fallback: carrega variáveis de ambiente do sistema sem disparar erro se o .env não for achado
        return Dotenv.configure().ignoreIfMissing().load();
    }

    private static String getConfig(String dotKey, String envKey) {
        String value = dotenv.get(dotKey);
        if (value == null || value.isBlank()) {
            value = dotenv.get(envKey);
        }
        if (value == null || value.isBlank()) {
            value = System.getenv(dotKey);
        }
        if (value == null || value.isBlank()) {
            value = System.getenv(envKey);
        }
        if (value == null || value.isBlank()) {
            value = readFromProcEnviron(dotKey, envKey);
        }
        if (value != null) {
            value = value.trim();
            // Remove prefixo acidental caso o usuário tenha colado "db.url=jdbc:..." no valor da variável
            if (value.startsWith(dotKey + "=")) {
                value = value.substring((dotKey + "=").length()).trim();
            } else if (value.startsWith(envKey + "=")) {
                value = value.substring((envKey + "=").length()).trim();
            }
            // Remove aspas acidentais nas extremidades
            if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1).trim();
            }
        }
        return value;
    }

    /**
     * Lê variáveis diretamente de /proc/1/environ em containers Linux caso o shell (/bin/sh)
     * do Tomcat tenha filtrado variáveis com ponto no nome (como db.url, db.user, db.password).
     */
    private static String readFromProcEnviron(String dotKey, String envKey) {
        File procEnv = new File("/proc/1/environ");
        if (!procEnv.exists() || !procEnv.canRead()) {
            return null;
        }
        try {
            String raw = java.nio.file.Files.readString(procEnv.toPath(), java.nio.charset.StandardCharsets.UTF_8);
            for (String entry : raw.split("\0")) {
                if (entry.startsWith(dotKey + "=")) {
                    return entry.substring((dotKey + "=").length());
                }
                if (entry.startsWith(envKey + "=")) {
                    return entry.substring((envKey + "=").length());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Inicializa o pool de conexões com parâmetros otimizados para banco em nuvem.
     */
    private static synchronized void initDataSource() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        String url = getConfig("db.url", "DB_URL");
        String user = getConfig("db.user", "DB_USER");
        String password = getConfig("db.password", "DB_PASSWORD");

        if (url == null || user == null || password == null || url.isBlank() || user.isBlank() || password.isBlank()) {
            System.err.println("[ConnectionFactory] ERRO: Credenciais ausentes (url=" + (url != null) + ", user=" + (user != null) + ", password=" + (password != null) + ").");
            throw new SQLException("Configure db.url, db.user e db.password no .env local ou DB_URL, DB_USER e DB_PASSWORD nas variáveis de ambiente.");
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("EfficientiaHikariPool");
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // Otimização de parâmetros do Pool para banco remoto na nuvem (Aiven PostgreSQL)
        config.setMaximumPoolSize(5);         // Limite seguro para não esgotar as 20 conexões do plano gratuito Aiven
        config.setMinimumIdle(1);             // Mantém conexão pronta sem sobrecarregar o limite simultâneo
        config.setIdleTimeout(60000);         // 1 minuto: encerra conexões excedentes ociosas
        config.setConnectionTimeout(20000);   // 20 segundos: tolerância para cold start em nuvem
        config.setMaxLifetime(1800000);       // 30 minutos: renova conexões antes de expiração de firewall/cloud
        config.setKeepaliveTime(60000);       // 1 minuto: ping periódico preventivo contra timeout de NAT

        try {
            dataSource = new HikariDataSource(config);
        } catch (RuntimeException ex) {
            System.err.println("[ConnectionFactory] Falha ao conectar ao banco PostgreSQL (" + url + "): " + ex.getMessage());
            throw new SQLException("Não foi possível estabelecer conexão com o banco de dados PostgreSQL.", ex);
        }
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
