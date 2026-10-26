package com.efficientia.efficientia.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitário para hash e verificação de CPF (LGPD / Proteção de Dados Sensíveis).
 *
 * Utiliza derivação criptográfica com salt individual (PBKDF2WithHmacSHA256)
 * seguindo o mesmo padrão do {@link PasswordHasher}, garantindo que CPFs não
 * sejam armazenados em texto claro.
 */
public final class CpfHasher {

    private static final int ITERATIONS = 600_000;
    private static final String PREFIX = "pbkdf2-sha256$";

    private CpfHasher() { }

    /**
     * Gera o hash criptográfico do CPF com salt seguro individual.
     * Sanitiza o CPF previamente extraindo apenas os dígitos.
     *
     * @param cpf string contendo o CPF formatado ou apenas dígitos
     * @return hash seguro prefixado com 'pbkdf2-sha256$'
     */
    public static String hash(String cpf) {
        if (cpf == null) {
            return null;
        }
        String limpo = sanitizar(cpf);
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return PREFIX + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derive(limpo, salt, ITERATIONS));
    }

    /**
     * Verifica se uma string de CPF armazenada já possui o formato de hash.
     *
     * @param stored valor armazenado no banco ou objeto
     * @return true se começar com o prefixo do algoritmo
     */
    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX);
    }

    /**
     * Valida se um CPF em texto claro corresponde a um hash (ou valor legado) armazenado.
     *
     * @param cpf    CPF fornecido (em texto claro)
     * @param stored hash seguro ou CPF em texto puro (legado)
     * @return true se coincidir, false caso contrário
     */
    public static boolean verify(String cpf, String stored) {
        if (cpf == null || stored == null) return false;
        String limpo = sanitizar(cpf);

        // Compatibilidade com cadastros legados em texto claro
        if (!isHashed(stored)) {
            String storedLimpo = sanitizar(stored);
            return MessageDigest.isEqual(
                    limpo.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    storedLimpo.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
        }

        try {
            String[] parts = stored.split("\\$", -1);
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 1 || iterations > 1_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return salt.length == 16 && expected.length == 32
                    && MessageDigest.isEqual(expected, derive(limpo, salt, iterations));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Extrai apenas dígitos numéricos do CPF, ignorando pontuação e espaços.
     */
    private static String sanitizar(String cpf) {
        return cpf.replaceAll("\\D", "").trim();
    }

    private static byte[] derive(String valor, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(valor.toCharArray(), salt, iterations, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Não foi possível gerar hash para o CPF.", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
