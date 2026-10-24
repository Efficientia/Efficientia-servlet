package com.efficientia.efficientia.util;

import com.efficientia.efficientia.exception.CnpjDuplicadoException;
import com.efficientia.efficientia.exception.CodigoDuplicadoException;
import com.efficientia.efficientia.exception.CpfDuplicadoException;
import com.efficientia.efficientia.exception.DadoDuplicadoException;
import com.efficientia.efficientia.exception.DocumentoFiscalDuplicadoException;
import com.efficientia.efficientia.exception.EmailDuplicadoException;
import com.efficientia.efficientia.exception.PlacaDuplicadaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Tratador de Exceções de Banco de Dados e Duplicidade")
class TratadorExcecaoBancoTest {

    @Test
    @DisplayName("Deve reconhecer violação de unicidade pelo SQLState 23505 (PostgreSQL)")
    void deveReconhecerSqlState23505() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"pecuarista_cpf_key\"\n" +
                        "  Detail: Key (cpf)=(12345678901) already exists.",
                "23505"
        );

        assertTrue(TratadorExcecaoBanco.isDuplicidade(sqlException));
    }

    @Test
    @DisplayName("Deve reconhecer erro de chave duplicada pelo código MySQL 1062")
    void deveReconhecerMySqlError1062() {
        SQLException sqlException = new SQLException("Duplicate entry '123' for key 'PRIMARY'", "23000", 1062);
        assertTrue(TratadorExcecaoBanco.isDuplicidade(sqlException));
    }

    @Test
    @DisplayName("Não deve classificar outros erros SQL como duplicidade")
    void naoDeveClassificarOutrosErrosComoDuplicidade() {
        SQLException erroConexao = new SQLException("Connection refused", "08001");
        assertFalse(TratadorExcecaoBanco.isDuplicidade(erroConexao));

        SQLException erroNulo = null;
        assertFalse(TratadorExcecaoBanco.isDuplicidade(erroNulo));
    }

    @Test
    @DisplayName("Deve traduzir constraint de CPF para CpfDuplicadoException com valor e detalhes extraídos")
    void deveTraduzirCpfDuplicado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"pecuarista_cpf_key\"\n" +
                        "  Detail: Key (cpf)=(12345678901) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = TratadorExcecaoBanco.traduzir(sqlException, "Pecuarista");

        assertInstanceOf(CpfDuplicadoException.class, ex);
        assertEquals("CPF", ex.getCampo());
        assertEquals("12345678901", ex.getValorDuplicado());
        assertEquals("pecuarista_cpf_key", ex.getConstraintName());
        assertEquals("cpf_duplicado", ex.getTipoErro());
        assertEquals("Pecuarista", ex.getEntidade());
    }

    @Test
    @DisplayName("Deve traduzir constraint de E-mail para EmailDuplicadoException")
    void deveTraduzirEmailDuplicado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"motorista_email_key\"\n" +
                        "  Detail: Key (email)=(joao@transportes.com) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = TratadorExcecaoBanco.traduzir(sqlException, "Motorista");

        assertInstanceOf(EmailDuplicadoException.class, ex);
        assertEquals("E-mail", ex.getCampo());
        assertEquals("joao@transportes.com", ex.getValorDuplicado());
        assertEquals("motorista_email_key", ex.getConstraintName());
        assertEquals("email_duplicado", ex.getTipoErro());
    }

    @Test
    @DisplayName("Deve traduzir constraint de CNPJ para CnpjDuplicadoException")
    void deveTraduzirCnpjDuplicado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"empresa_cnpj_key\"\n" +
                        "  Detail: Key (cnpj)=(12345678000101) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = TratadorExcecaoBanco.traduzir(sqlException, "Empresa");

        assertInstanceOf(CnpjDuplicadoException.class, ex);
        assertEquals("CNPJ", ex.getCampo());
        assertEquals("12345678000101", ex.getValorDuplicado());
        assertEquals("empresa_cnpj_key", ex.getConstraintName());
        assertEquals("cnpj_duplicado", ex.getTipoErro());
    }

    @Test
    @DisplayName("Deve traduzir constraint de Placa para PlacaDuplicadaException")
    void deveTraduzirPlacaDuplicada() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"caminhao_placa_carreta_key\"\n" +
                        "  Detail: Key (placa_carreta)=(BRA1A12) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = TratadorExcecaoBanco.traduzir(sqlException, "Caminhão");

        assertInstanceOf(PlacaDuplicadaException.class, ex);
        assertEquals("Placa Carreta", ex.getCampo());
        assertEquals("BRA1A12", ex.getValorDuplicado());
        assertEquals("placa_duplicada", ex.getTipoErro());
    }

    @Test
    @DisplayName("Deve traduzir constraint de Código Corporativo para CodigoDuplicadoException")
    void deveTraduzirCodigoDuplicado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"analista_codigo_key\"\n" +
                        "  Detail: Key (codigo)=(ANA-001) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = TratadorExcecaoBanco.traduzir(sqlException, "Analista");

        assertInstanceOf(CodigoDuplicadoException.class, ex);
        assertEquals("Código", ex.getCampo());
        assertEquals("ANA-001", ex.getValorDuplicado());
        assertEquals("codigo_duplicado", ex.getTipoErro());
    }

    @Test
    @DisplayName("Deve traduzir GTA ou NF-e para DocumentoFiscalDuplicadoException")
    void deveTraduzirDocumentoFiscalDuplicado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"trajeto_numero_gta_key\"\n" +
                        "  Detail: Key (numero_gta)=(GTA-12345) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = TratadorExcecaoBanco.traduzir(sqlException, "Trajeto");

        assertInstanceOf(DocumentoFiscalDuplicadoException.class, ex);
        assertEquals("documento_fiscal_duplicado", ex.getTipoErro());
        assertEquals("GTA-12345", ex.getValorDuplicado());
    }

    @Test
    @DisplayName("tratarSeDuplicado deve lançar exceção se for duplicidade e silenciar se for outro erro")
    void deveLancarApenasSeDuplicado() {
        SQLException erroDuplicidade = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"pecuarista_cpf_key\"",
                "23505"
        );

        assertThrows(CpfDuplicadoException.class, () ->
                TratadorExcecaoBanco.tratarSeDuplicado(erroDuplicidade, "Pecuarista")
        );

        SQLException outroErro = new SQLException("Tabela não encontrada", "42P01");
        assertDoesNotThrow(() ->
                TratadorExcecaoBanco.tratarSeDuplicado(outroErro, "Pecuarista")
        );
    }
}
