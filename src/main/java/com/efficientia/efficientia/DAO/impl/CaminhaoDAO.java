package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.CaminhaoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Caminhão.
 *
 * Responsável por encapsular as operações de persistência e manipulação
 * de dados da tabela 'caminhao' no PostgreSQL, gerenciando conexões
 * via ConnectionFactory e garantindo fechamento de recursos com try-with-resources.
 */
public class CaminhaoDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo registro de caminhão no banco de dados.
     *
     * @param caminhaoModel objeto contendo os dados do caminhão a ser inserido
     * @return true se o registro foi inserido com sucesso, false caso ocorra falha
     */
    public boolean inserir(CaminhaoModel caminhaoModel) {
        String sql = """
                INSERT INTO caminhao (
                    placa_cavalo,
                    placa_carreta,
                    capacidade_maxima
                )
                VALUES (?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, caminhaoModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Caminhao: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos os caminhões cadastrados no banco de dados, ordenados por ID.
     *
     * @return lista contendo os caminhões encontrados ou lista vazia em caso de falha/ausência
     */
    public List<CaminhaoModel> listar() {
        String sql = """
                SELECT * FROM caminhao ORDER BY id;
                """;

        List<CaminhaoModel> caminhaoModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CaminhaoModel caminhaoModel = new CaminhaoModel(
                        rs.getInt("id"),
                        rs.getString("placa_cavalo"),
                        rs.getString("placa_carreta"),
                        rs.getInt("capacidade_maxima")
                );

                caminhaoModels.add(caminhaoModel);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Caminhao: " + e.getMessage());
        }

        return caminhaoModels;
    }

    /**
     * Atualiza os dados de um caminhão existente a partir do seu identificador.
     *
     * @param caminhaoModel objeto com os novos dados a serem gravados
     * @param id            identificador único do caminhão a ser atualizado
     * @return true se a alteração foi realizada com sucesso, false caso contrário
     */
    public boolean atualizar(CaminhaoModel caminhaoModel, int id) {
        String sql = """
                UPDATE caminhao
                SET placa_cavalo = ?,
                    placa_carreta = ?,
                    capacidade_maxima = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, caminhaoModel);
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Caminhao: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um registro de caminhão da base de dados com base no ID fornecido.
     *
     * @param id identificador único do caminhão a ser excluído
     * @return true se o registro foi removido com sucesso, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM caminhao WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir Caminhao: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza e retorna um caminhão específico a partir de seu identificador único.
     *
     * @param id identificador único do caminhão buscado
     * @return objeto CaminhaoModel correspondente ou null se não for encontrado
     */
    public CaminhaoModel buscar(int id) {
        String sql = """
                SELECT * FROM caminhao WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CaminhaoModel(
                            rs.getInt("id"),
                            rs.getString("placa_cavalo"),
                            rs.getString("placa_carreta"),
                            rs.getInt("capacidade_maxima")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Caminhao: " + e.getMessage());
        }

        return null;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Mapeia os dados do modelo nos parâmetros posicionais do PreparedStatement.
     *
     * @param stmt           statement preparado para receber os parâmetros
     * @param caminhaoModel  objeto com os dados a serem vinculados
     * @throws SQLException se ocorrer falha ao atribuir os valores no JDBC
     */
    private void preencherStatement(
            PreparedStatement stmt,
            CaminhaoModel caminhaoModel
    ) throws SQLException {
        stmt.setString(1, caminhaoModel.getPlacaCavalo());
        stmt.setString(2, caminhaoModel.getPlacaCarreta());
        stmt.setInt(3, caminhaoModel.getCapacidadeMaxima());
    }
}
