package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AnalistaModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Analista.
 *
 * Centraliza as operações de persistência e acesso à tabela 'analista'
 * no banco de dados relacional PostgreSQL, gerenciando a abertura e fechamento
 * seguro de recursos através de ConnectionFactory e blocos try-with-resources.
 */
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class AnalistaDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo analista no banco de dados.
     *
     * @param analistaModel objeto AnalistaModel contendo os dados cadastrais do analista
     * @return true se o registro foi inserido com êxito, false caso ocorra falha ou o modelo seja nulo
     */
    public boolean inserir(AnalistaModel analistaModel) {
        if (analistaModel == null) return false;

        String sql = """
                INSERT INTO analista(
                    cpf,
                    nome,
                    data_nascimento,
                    senha,
                    email,
                    telefone,
                    codigo
                ) VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, analistaModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Analista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos os analistas cadastrados no banco de dados, ordenados por ID.
     *
     * @return lista contendo os analistas encontrados ou lista vazia em caso de falha/ausência de registros
     */
    public List<AnalistaModel> listar() {
        String sql = """
                SELECT * FROM analista ORDER BY id;
                """;
        List<AnalistaModel> listaAnalista = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Date dataNasc = rs.getDate("data_nascimento");
                AnalistaModel analistaModel = new AnalistaModel(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        rs.getString("assinatura"),
                        dataNasc != null ? dataNasc.toLocalDate() : null,
                        rs.getString("senha"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("codigo")
                );
                listaAnalista.add(analistaModel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Analista: " + e.getMessage());
        }
        return listaAnalista;
    }

    /**
     * Atualiza os dados de um analista existente com base em seu ID.
     *
     * @param analistaModel objeto contendo os novos dados do analista
     * @param id            identificador numérico do analista a ser modificado
     * @return true se o registro foi atualizado com sucesso, false caso ocorra falha ou o modelo seja nulo
     */
    public boolean atualizar(AnalistaModel analistaModel, int id) {
        if (analistaModel == null) return false;

        String sql = """
                UPDATE analista SET
                    cpf = ?,
                    nome = ?,
                    data_nascimento = ?,
                    senha = ?,
                    email = ?,
                    telefone = ?,
                    codigo = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            preencherStatement(stmt, analistaModel);
            stmt.setInt(8, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Analista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um registro de analista do banco de dados pelo seu ID.
     *
     * @param id identificador único do analista a ser excluído
     * @return true se a exclusão for efetuada com sucesso, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM analista WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir Analista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza um analista pelo seu identificador único.
     *
     * @param id identificador único do analista
     * @return objeto AnalistaModel se encontrado, ou null caso contrário
     */
    public AnalistaModel buscar(int id) {
        String sql = """
                SELECT * FROM analista WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dataNasc = rs.getDate("data_nascimento");
                    return new AnalistaModel(
                            rs.getInt("id"),
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            rs.getString("assinatura"),
                            dataNasc != null ? dataNasc.toLocalDate() : null,
                            rs.getString("senha"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getString("codigo")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca analistas por nome, suportando correspondência exata, parcial ("picada")
     * e case-insensitive (ignorando maiúsculas e minúsculas).
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de analistas encontrados
     */
    public List<AnalistaModel> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }

        String[] tokens = termo.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM analista
                WHERE 1=1
                """);

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(nome) LIKE ?");
        }
        sql.append(" ORDER BY nome ASC, id ASC;");

        List<AnalistaModel> listaAnalista = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date dataNasc = rs.getDate("data_nascimento");
                    AnalistaModel analistaModel = new AnalistaModel(
                            rs.getInt("id"),
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            rs.getString("assinatura"),
                            dataNasc != null ? dataNasc.toLocalDate() : null,
                            rs.getString("senha"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getString("codigo")
                    );
                    listaAnalista.add(analistaModel);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por nome: " + e.getMessage());
        }

        return listaAnalista;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Mapeia os atributos do modelo AnalistaModel para os parâmetros do PreparedStatement.
     *
     * @param stmt          PreparedStatement configurado com a query SQL
     * @param analistaModel objeto contendo os dados do analista
     * @throws SQLException se ocorrer erro durante a parametrização
     */
    private void preencherStatement(PreparedStatement stmt, AnalistaModel analistaModel) throws SQLException {
        stmt.setString(1, analistaModel.getCpf());
        stmt.setString(2, analistaModel.getNome());
        stmt.setDate(3, analistaModel.getDataNascimento() != null ? Date.valueOf(analistaModel.getDataNascimento()) : null);
        stmt.setString(4, analistaModel.getSenha());
        stmt.setString(5, analistaModel.getEmail());
        stmt.setString(6, analistaModel.getTelefone());
        stmt.setString(7, analistaModel.getCodigo());
    }
}