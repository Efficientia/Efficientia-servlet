package com.efficientia.efficientia.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários do ValidadorRegex — Demandas de Sistemas Operacionais e Lógica")
class ValidadorRegexTest {

    // ==================== TESTES DE CPF ====================

    @Test
    @DisplayName("Deve validar CPFs com formatação e dígitos puros")
    void testCpfValido() {
        assertTrue(ValidadorRegex.isCpfValido("123.456.789-00"));
        assertTrue(ValidadorRegex.isCpfValido("12345678900"));
        assertTrue(ValidadorRegex.isCpfValido("000.000.000-00"));

        assertFalse(ValidadorRegex.isCpfValido("123.456.789-0"));   // Poucos dígitos
        assertFalse(ValidadorRegex.isCpfValido("1234567890"));      // 10 dígitos
        assertFalse(ValidadorRegex.isCpfValido("123456789001"));    // 12 dígitos
        assertFalse(ValidadorRegex.isCpfValido("123.456.789-0A"));  // Caractere alfabético
        assertFalse(ValidadorRegex.isCpfValido(null));
        assertFalse(ValidadorRegex.isCpfValido(""));
        assertFalse(ValidadorRegex.isCpfValido("   "));
    }

    // ==================== TESTES DE TELEFONE / CELULAR ====================

    @Test
    @DisplayName("Deve validar celulares (9 dígitos + DDD) e fixos (8 dígitos + DDD) sem código de país +55")
    void testTelefoneECelularValido() {
        // Celulares com DDD
        assertTrue(ValidadorRegex.isCelularValido("(11) 98765-4321"));
        assertTrue(ValidadorRegex.isCelularValido("11 98765-4321"));
        assertTrue(ValidadorRegex.isCelularValido("11987654321"));
        assertTrue(ValidadorRegex.isCelularValido("(67) 99123-4567"));

        // Telefones fixos com DDD
        assertTrue(ValidadorRegex.isCelularValido("(11) 3456-7890"));
        assertTrue(ValidadorRegex.isCelularValido("1134567890"));

        // Rejeição estrita de DDI (+55) conforme especificação do edital de Sistemas Operacionais
        assertFalse(ValidadorRegex.isCelularValido("+55 11 98765-4321"));
        assertFalse(ValidadorRegex.isCelularValido("+5511987654321"));

        // Inválidos: números sem DDD ou quantidade errada de dígitos
        assertFalse(ValidadorRegex.isCelularValido("98765-4321"));  // Sem DDD
        assertFalse(ValidadorRegex.isCelularValido("119876"));      // Incompleto
        assertFalse(ValidadorRegex.isCelularValido(null));
        assertFalse(ValidadorRegex.isCelularValido(""));
    }

    // ==================== TESTES DE PLACAS VEICULARES ====================

    @Test
    @DisplayName("Deve validar placas nos padrões Mercosul e Tradicional Brasileiro")
    void testPlacaValida() {
        // Padrão Mercosul (ABC1D23)
        assertTrue(ValidadorRegex.isPlacaValida("BRA1A12"));
        assertTrue(ValidadorRegex.isPlacaValida("bra1a12")); // Insensível a maiúsculas/minúsculas
        assertTrue(ValidadorRegex.isPlacaValida("XYZ9Z99"));

        // Padrão Tradicional (ABC-1234 ou ABC1234)
        assertTrue(ValidadorRegex.isPlacaValida("ABC-1234"));
        assertTrue(ValidadorRegex.isPlacaValida("ABC1234"));

        // Inválidas
        assertFalse(ValidadorRegex.isPlacaValida("AB12345"));
        assertFalse(ValidadorRegex.isPlacaValida("ABCD-1234"));
        assertFalse(ValidadorRegex.isPlacaValida("BRA12345"));
        assertFalse(ValidadorRegex.isPlacaValida(null));
        assertFalse(ValidadorRegex.isPlacaValida(""));
    }

    // ==================== TESTES DE E-MAIL ====================

    @Test
    @DisplayName("Deve validar sintaxe de e-mails em conformidade com padrões de mercado")
    void testEmailValido() {
        assertTrue(ValidadorRegex.isEmailValido("contato@efficientia.com.br"));
        assertTrue(ValidadorRegex.isEmailValido("usuario.teste+label@empresa.com"));
        assertTrue(ValidadorRegex.isEmailValido("motorista123@gmail.com"));

        assertFalse(ValidadorRegex.isEmailValido("email_sem_arroba.com"));
        assertFalse(ValidadorRegex.isEmailValido("@dominio.com"));
        assertFalse(ValidadorRegex.isEmailValido("usuario@"));
        assertFalse(ValidadorRegex.isEmailValido("usuario@dominio")); // Sem TLD
        assertFalse(ValidadorRegex.isEmailValido(null));
        assertFalse(ValidadorRegex.isEmailValido(""));
    }

    // ==================== TESTES DE CNPJ ====================

    @Test
    @DisplayName("Deve validar CNPJs formatados e contínuos de 14 dígitos")
    void testCnpjValido() {
        assertTrue(ValidadorRegex.isCnpjValido("12.345.678/0001-01"));
        assertTrue(ValidadorRegex.isCnpjValido("12345678000101"));

        assertFalse(ValidadorRegex.isCnpjValido("1234567800010"));   // 13 dígitos
        assertFalse(ValidadorRegex.isCnpjValido("123456780001019")); // 15 dígitos
        assertFalse(ValidadorRegex.isCnpjValido("12.345.678/0001-0A"));
        assertFalse(ValidadorRegex.isCnpjValido(null));
        assertFalse(ValidadorRegex.isCnpjValido(""));
    }

    // ==================== TESTES DE SANITIZAÇÃO E NORMALIZAÇÃO ====================

    @Test
    @DisplayName("Deve higienizar strings extraindo somente os dígitos numéricos")
    void testApenasDigitos() {
        assertEquals("12345678900", ValidadorRegex.apenasDigitos("123.456.789-00"));
        assertEquals("11987654321", ValidadorRegex.apenasDigitos("(11) 98765-4321"));
        assertEquals("12345678000101", ValidadorRegex.apenasDigitos("12.345.678/0001-01"));
        assertEquals("", ValidadorRegex.apenasDigitos(null));
        assertEquals("", ValidadorRegex.apenasDigitos("texto sem numeros"));
    }

    @Test
    @DisplayName("Deve normalizar placas veiculares em maiúsculas e sem hífen")
    void testNormalizarPlaca() {
        assertEquals("BRA1A12", ValidadorRegex.normalizarPlaca("bra-1a12"));
        assertEquals("ABC1234", ValidadorRegex.normalizarPlaca("abc-1234"));
        assertEquals("BRA1A12", ValidadorRegex.normalizarPlaca("  bra1a12  "));
        assertEquals("", ValidadorRegex.normalizarPlaca(null));
    }
}
