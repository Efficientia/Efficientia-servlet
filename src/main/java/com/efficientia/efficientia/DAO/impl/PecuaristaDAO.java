package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.PecuaristaModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Pecuarista.
 *
 * Responsável pelas operações de persistência e consulta dos pecuaristas
 * (produtores rurais) na tabela 'pecuarista' do banco de dados relacional PostgreSQL.
 */
public class PecuaristaDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo pecuarista no banco de dados.
     *
     * @param pecuaristaModel objeto PecuaristaModel com os dados cadastrais
     * @return true se o pecuarista foi inserido com êxito, false caso ocorra falha
     */
    public boolean inserir(PecuaristaModel pecuaristaModel) {
        String sql = """
                INSERT INTO pecuarista (cpf,
                                      assinatura,
                                      data_nascimento,
                                      nome,
                                      senha,
                                      email,
                                      telefone)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, pecuaristaModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos os pecuaristas registrados no banco de dados, ordenados por ID.
     *
     * @return lista contendo os pecuaristas encontrados ou lista vazia em caso de ausência/erro
     */
    public List<PecuaristaModel> listar() {
        String sql = """
                SELECT * FROM pecuarista
                ORDER BY id;
                """;

        List<PecuaristaModel> listaPecuarista = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date dataNascimento = rs.getDate("data_nascimento");

                PecuaristaModel pecuaristaModel = new PecuaristaModel(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("assinatura"),
                        dataNascimento != null ? dataNascimento.toLocalDate() : null,
                        rs.getString("nome"),
                        rs.getString("senha"),
                        rs.getString("email"),
                        rs.getString("telefone")
                );
                listaPecuarista.add(pecuaristaModel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Pecuarista: " + e.getMessage());
        }

        return listaPecuarista;
    }

    /**
     * Atualiza os dados de um pecuarista previamente cadastrado.
     *
     * @param pecuaristaModel objeto com os novos valores para atualização
     * @param id              identificador único do pecuarista a ser atualizado
     * @return true se o registro foi atualizado com sucesso, false caso contrário
     */
    public boolean atualizar(PecuaristaModel pecuaristaModel, int id) {
        String sql = """
                UPDATE pecuarista
                SET cpf = ?,
                    assinatura = ?,
                    data_nascimento = ?,
                    nome = ?,
                    senha = ?,
                    email = ?,
                    telefone = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, pecuaristaModel);
            stmt.setInt(8, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um pecuarista da base de dados com base no ID informado.
     *
     * @param id identificador único do pecuarista a ser excluído
     * @return true se o registro foi removido com êxito, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM pecuarista WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza um pecuarista a partir do seu identificador único.
     *
     * @param id identificador único do pecuarista
     * @return objeto PecuaristaModel correspondente ou null se não for localizado
     */
    public PecuaristaModel buscar(int id) {
        String sql = """
                SELECT * FROM pecuarista WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dataNascimento = rs.getDate("data_nascimento");
                    LocalDate nascimento = dataNascimento != null ? dataNascimento.toLocalDate() : null;

                    return new PecuaristaModel(
                            rs.getInt("id"),
                            rs.getString("cpf"),
                            rs.getString("assinatura"),
                            nascimento,
                            rs.getString("nome"),
                            rs.getString("senha"),
                            rs.getString("email"),
                            rs.getString("telefone")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuarista: " + e.getMessage());
            return null;
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Atribui os campos de PecuaristaModel aos parâmetros indexados do PreparedStatement.
     *
     * @param stmt            PreparedStatement pronto para recepção dos parâmetros
     * @param pecuaristaModel modelo com os dados cadastrais
     * @throws SQLException se houver erro durante a associação dos tipos JDBC
     */
    private void preencherStatement(PreparedStatement stmt, PecuaristaModel pecuaristaModel) throws SQLException {
        stmt.setString(1, pecuaristaModel.getCpf());
        stmt.setString(2, pecuaristaModel.getAssinatura());

        if (pecuaristaModel.getDataNascimento() != null) {
            stmt.setDate(3, Date.valueOf(pecuaristaModel.getDataNascimento()));
        } else {
            stmt.setDate(3, null);
        }

        stmt.setString(4, pecuaristaModel.getNome());
        stmt.setString(5, pecuaristaModel.getSenha());
        stmt.setString(6, pecuaristaModel.getEmail());
        stmt.setString(7, pecuaristaModel.getTelefone());
    }
}
