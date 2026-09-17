package com.efficientia.efficientia.factory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionFactory {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static final Dotenv dotenv = loadDotenv();

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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                dotenv.get("db.url"),
                dotenv.get("db.user"),
                dotenv.get("db.password")
        );
    }
}
