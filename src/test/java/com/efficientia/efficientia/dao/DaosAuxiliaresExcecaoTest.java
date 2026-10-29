package com.efficientia.efficientia.dao;

import com.efficientia.efficientia.exception.DadoDuplicadoException;
import com.efficientia.efficientia.util.TratadorExcecaoBanco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Tratamento de Exceções Padronizadas nos DAOs Auxiliares")
class DaosAuxiliaresExcecaoTest {

    @Test
    @DisplayName("Deve disparar DadoDuplicadoException quando PropriedadeDAO violar unique constraint de id_endereco")
    void deveLancarDadoDuplicadoParaPropriedadeComEnderecoJaVinculado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"propriedade_id_endereco_key\"\n" +
                        "  Detail: Key (id_endereco)=(10) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = assertThrows(
                DadoDuplicadoException.class,
                () -> TratadorExcecaoBanco.tratarSeDuplicado(sqlException, "Propriedade")
        );

        assertEquals("Propriedade", ex.getEntidade());
        assertEquals("propriedade_id_endereco_key", ex.getConstraintName());
        assertTrue(ex.getMessage().contains("Propriedade") || ex.getMessage().contains("já cadastrado"));
    }

    @Test
    @DisplayName("Deve disparar DadoDuplicadoException quando InfoEmbarqueDAO violar unique constraint de id_trajeto")
    void deveLancarDadoDuplicadoParaInfoEmbarqueComTrajetoDuplicado() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"info_embarque_id_trajeto_key\"\n" +
                        "  Detail: Key (id_trajeto)=(55) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = assertThrows(
                DadoDuplicadoException.class,
                () -> TratadorExcecaoBanco.tratarSeDuplicado(sqlException, "InfoEmbarque")
        );

        assertEquals("InfoEmbarque", ex.getEntidade());
        assertEquals("info_embarque_id_trajeto_key", ex.getConstraintName());
    }

    @Test
    @DisplayName("Deve disparar DadoDuplicadoException quando EnderecoDAO violar chave primária ou constraint")
    void deveLancarDadoDuplicadoParaEndereco() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"endereco_pkey\"\n" +
                        "  Detail: Key (id)=(99) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = assertThrows(
                DadoDuplicadoException.class,
                () -> TratadorExcecaoBanco.tratarSeDuplicado(sqlException, "Endereço")
        );

        assertEquals("Endereço", ex.getEntidade());
    }

    @Test
    @DisplayName("Deve disparar DadoDuplicadoException quando ParadaImprevistaDAO violar chave primária ou constraint")
    void deveLancarDadoDuplicadoParaParadaImprevista() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"parada_imprevista_pkey\"\n" +
                        "  Detail: Key (id)=(88) already exists.",
                "23505"
        );

        DadoDuplicadoException ex = assertThrows(
                DadoDuplicadoException.class,
                () -> TratadorExcecaoBanco.tratarSeDuplicado(sqlException, "ParadaImprevista")
        );

        assertEquals("ParadaImprevista", ex.getEntidade());
    }

    @Test
    @DisplayName("Não deve disparar exceção para erros SQL que não sejam de unicidade (ex: falha de conexão)")
    void naoDeveLancarExcecaoParaErrosQueNaoForemDuplicidade() {
        SQLException erroConexao = new SQLException("Connection refused / timeout", "08001");

        assertDoesNotThrow(() -> {
            TratadorExcecaoBanco.tratarSeDuplicado(erroConexao, "Propriedade");
            TratadorExcecaoBanco.tratarSeDuplicado(erroConexao, "InfoEmbarque");
            TratadorExcecaoBanco.tratarSeDuplicado(erroConexao, "Endereço");
            TratadorExcecaoBanco.tratarSeDuplicado(erroConexao, "ParadaImprevista");
        });
    }
}
