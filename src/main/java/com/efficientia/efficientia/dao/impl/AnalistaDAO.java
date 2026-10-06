package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AnalistaModel;
import com.efficientia.efficientia.model.EmpresaModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Analista.
 *
 * Centraliza as operações de persistência e acesso à tabela 'analista'
 * no banco de dados relacional PostgreSQL, realizando também a junção com a
 * tabela 'empresa' via ConnectionFactory e blocos try-with-resources.
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
                INSERT INTO analista (
                    id_empresa,
                    cpf,
                    nome,
                    data_nascimento,
                    senha,
                    email,
                    telefone,
                    codigo
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
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
     * Recupera todos os analistas cadastrados no banco de dados com suas empresas vinculadas, ordenados por ID.
     *
     * @return lista contendo os analistas encontrados ou lista vazia em caso de falha/ausência de registros
     */
    public List<AnalistaModel> listar() {
        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                ORDER BY a.id;
                """;
        List<AnalistaModel> listaAnalista = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaAnalista.add(construirAnalista(rs));
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
                    id_empresa = ?,
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
            stmt.setInt(9, id);
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
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE a.id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirAnalista(rs);
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
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE 1=1
                """);

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(a.nome) LIKE ?");
        }
        sql.append(" ORDER BY a.nome ASC, a.id ASC;");

        List<AnalistaModel> listaAnalista = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaAnalista.add(construirAnalista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por nome: " + e.getMessage());
        }

        return listaAnalista;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Constrói uma instância de AnalistaModel a partir do ResultSet atual com dados de Empresa.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto AnalistaModel populado
     * @throws SQLException se ocorrer erro de leitura do ResultSet
     */
    private AnalistaModel construirAnalista(ResultSet rs) throws SQLException {
        EmpresaModel empresaModel = null;
        int idEmpresa = rs.getInt("empresa_id");
        if (!rs.wasNull()) {
            empresaModel = new EmpresaModel(
                    idEmpresa,
                    rs.getString("empresa_nome"),
                    rs.getString("empresa_cnpj"),
                    rs.getString("empresa_codigo")
            );
        }

        Date dataNasc = rs.getDate("data_nascimento");

        return new AnalistaModel(
                rs.getInt("id"),
                empresaModel,
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

    /**
     * Mapeia os atributos do modelo AnalistaModel para os parâmetros do PreparedStatement.
     *
     * @param stmt          PreparedStatement configurado com a query SQL
     * @param analistaModel objeto contendo os dados do analista
     * @throws SQLException se ocorrer erro durante a parametrização
     */
    private void preencherStatement(PreparedStatement stmt, AnalistaModel analistaModel) throws SQLException {
        if (analistaModel.getEmpresaModel() != null) {
            stmt.setInt(1, analistaModel.getEmpresaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }
        stmt.setString(2, analistaModel.getCpf());
        stmt.setString(3, analistaModel.getNome());
        stmt.setDate(4, analistaModel.getDataNascimento() != null ? Date.valueOf(analistaModel.getDataNascimento()) : null);
        stmt.setString(5, analistaModel.getSenha());
        stmt.setString(6, analistaModel.getEmail());
        stmt.setString(7, analistaModel.getTelefone());
        stmt.setString(8, analistaModel.getCodigo());
    }
}