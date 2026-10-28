package com.efficientia.efficientia.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpfHasherTest {

    @Test
    void hashValidaSomenteCpfCorreto() {
        String cpfFormatado = "123.456.789-01";
        String hash = CpfHasher.hash(cpfFormatado);

        assertTrue(CpfHasher.isHashed(hash));
        assertTrue(CpfHasher.verify("123.456.789-01", hash));
        assertTrue(CpfHasher.verify("12345678901", hash)); // Sanitização ignora pontuação
        assertFalse(CpfHasher.verify("98765432100", hash));
    }

    @Test
    void saltsSaoIndividuaisParaMesmoCpf() {
        String cpf = "12345678901";
        assertNotEquals(CpfHasher.hash(cpf), CpfHasher.hash(cpf));
    }

    @Test
    void rejeitaHashMalformadoOuNulo() {
        assertFalse(CpfHasher.verify("12345678901", "pbkdf2-sha256$600000$???$???"));
        assertFalse(CpfHasher.verify("12345678901", "pbkdf2-sha256$999999999$a$b"));
        assertFalse(CpfHasher.verify(null, null));
        assertFalse(CpfHasher.verify("12345678901", null));
        assertFalse(CpfHasher.verify(null, "pbkdf2-sha256$..."));
    }

    @Test
    void permiteValidarLegadoEmTextoClaroParaMigracao() {
        assertTrue(CpfHasher.verify("123.456.789-01", "12345678901"));
        assertTrue(CpfHasher.verify("12345678901", "123.456.789-01"));
        assertFalse(CpfHasher.verify("12345678901", "98765432100"));
    }
}
