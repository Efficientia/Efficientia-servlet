package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EmpresaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Empresa.
 *
 * Centraliza as operações de persistência e acesso à tabela 'empresa'
 * no banco de dados PostgreSQL, gerenciando abertura e liberação de conexões
 * através de ConnectionFactory e blocos try-with-resources.
 */
public class EmpresaDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere uma nova empresa na base de dados.
     *
     * @param empresa objeto EmpresaModel contendo os dados a serem gravados
     * @return true se a inserção for realizada com êxito, false em caso de falha
     */
    public boolean inserir(EmpresaModel empresa) {
        String sql = """
                INSERT INTO empresa (nome, cnpj)
                VALUES (?, ?);
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, empresa);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir empresaModel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todas as empresas cadastradas no banco de dados ordenadas por ID.
     *
     * @return lista contendo os registros de empresas ou lista vazia caso não haja registros
     */
    public List<EmpresaModel> listar() {
        String sql = """
                SELECT * FROM empresa
                ORDER BY id;
                """;

        List<EmpresaModel> empresas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EmpresaModel empresaModel = new EmpresaModel(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("codigo")
                );

                empresas.add(empresaModel);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar empresaModel: " + e.getMessage());
        }

        return empresas;
    }

    /**
     * Atualiza os dados de uma empresa existente com base em seu ID.
     *
     * @param empresa objeto com os novos valores para atualização
     * @param id      identificador numérico da empresa a ser modificada
     * @return true se o registro foi atualizado com sucesso, false caso contrário
     */
    public boolean atualizar(EmpresaModel empresa, int id) {
        String sql = """
                UPDATE empresa
                SET nome = ?, 
                    cnpj = ? 
                WHERE id = ?;
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, empresa);
            stmt.setInt(3, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar empresaModel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um registro de empresa do banco de dados pelo seu ID.
     *
     * @param id identificador único da empresa
     * @return true se o registro foi excluído, false em caso de erro
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM empresa WHERE id = ?;
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir empresaModel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca uma empresa pelo seu identificador único.
     *
     * @param id identificador único da empresa
     * @return objeto EmpresaModel se encontrado, ou null caso contrário
     */
    public EmpresaModel buscar(int id) {
        String sql = """
                SELECT * FROM empresa WHERE id = ?;
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmpresaModel(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cnpj"),
                            rs.getString("codigo")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresaModel: " + e.getMessage());
        }

        return null;
    }

    /**
     * Busca uma empresa pelo seu código identificador único corporativo existente no banco.
     *
     * @param codigo código identificador da empresa
     * @return objeto EmpresaModel se encontrado, ou null caso contrário
     */
    public EmpresaModel buscarPorCodigo(String codigo) {
        String sql = """
                SELECT * FROM empresa WHERE codigo = ?;
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmpresaModel(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cnpj"),
                            rs.getString("codigo")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresaModel por codigo: " + e.getMessage());
        }

        return null;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Mapeia os atributos do modelo EmpresaModel para os parâmetros do PreparedStatement.
     *
     * @param stmt    PreparedStatement configurado com a query SQL
     * @param empresa objeto com os dados da empresa
     * @throws SQLException se ocorrer erro durante a parametrização
     */
    private void preencherStatement(PreparedStatement stmt, EmpresaModel empresa) throws SQLException {
        stmt.setString(1, empresa.getNome());
        stmt.setString(2, empresa.getCnpj());
    }
}
