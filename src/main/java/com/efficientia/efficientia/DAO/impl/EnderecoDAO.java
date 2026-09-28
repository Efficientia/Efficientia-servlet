package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EnderecoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Endereço.
 *
 * Gerencia a persistência de endereços residenciais, corporativos e de propriedades rurais
 * na tabela 'endereco' do banco de dados relacional PostgreSQL, utilizando JDBC e ConnectionFactory.
 */
public class EnderecoDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo endereço no banco de dados.
     *
     * @param enderecoModel objeto contendo todos os dados do logradouro e localização
     * @return true se a inserção for realizada com êxito, false em caso de falha
     */
    public boolean inserir(EnderecoModel enderecoModel) {
        String sql = """
                INSERT INTO endereco (cep,
                                      tipo,
                                      numero,
                                      rua,
                                      cidade,
                                      estado,
                                      pais,
                                      complemento)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, enderecoModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Endereco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna a listagem completa de todos os endereços gravados na base.
     *
     * @return lista de objetos EnderecoModel ordenada crescentemente por ID
     */
    public List<EnderecoModel> listar() {
        String sql = """
                SELECT * FROM endereco ORDER BY id;
                """;
        List<EnderecoModel> enderecoModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EnderecoModel enderecoModel = new EnderecoModel(
                        rs.getInt("id"),
                        rs.getString("cep"),
                        rs.getString("tipo"),
                        rs.getString("numero"),
                        rs.getString("rua"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("pais"),
                        rs.getString("complemento")
                );
                enderecoModels.add(enderecoModel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Endereco: " + e.getMessage());
        }
        return enderecoModels;
    }

    /**
     * Atualiza as informações de um endereço cadastrado a partir de seu ID.
     *
     * @param enderecoModel objeto com os novos dados de localização
     * @param id            identificador numérico do endereço a ser atualizado
     * @return true se o registro foi atualizado com sucesso, false caso contrário
     */
    public boolean atualizar(EnderecoModel enderecoModel, int id) {
        String sql = """
                UPDATE endereco SET
                    cep = ?,
                    tipo = ?,
                    numero = ?,
                    rua = ?,
                    cidade = ?,
                    estado = ?,
                    pais = ?,
                    complemento = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, enderecoModel);
            stmt.setInt(9, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Endereco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um endereço do banco de dados pelo seu ID.
     *
     * @param id identificador único do endereço a ser excluído
     * @return true se o registro foi removido com sucesso, false caso ocorra erro
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM endereco WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir Endereco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza um endereço individual com base em seu ID.
     *
     * @param id identificador único do endereço pesquisado
     * @return objeto EnderecoModel preenchido ou null se não for encontrado
     */
    public EnderecoModel buscar(int id) {
        String sql = """
                SELECT * FROM endereco WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EnderecoModel(
                            rs.getInt("id"),
                            rs.getString("cep"),
                            rs.getString("tipo"),
                            rs.getString("numero"),
                            rs.getString("rua"),
                            rs.getString("cidade"),
                            rs.getString("estado"),
                            rs.getString("pais"),
                            rs.getString("complemento")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Endereco: " + e.getMessage());
            return null;
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Preenche os parâmetros do PreparedStatement com os atributos de EnderecoModel.
     *
     * @param stmt     PreparedStatement associado à query SQL
     * @param endereco modelo com os dados a serem vinculados
     * @throws SQLException em caso de erro na vinculação dos parâmetros
     */
    private void preencherStatement(PreparedStatement stmt, EnderecoModel endereco) throws SQLException {
        stmt.setString(1, endereco.getCep());
        stmt.setString(2, endereco.getTipo());
        stmt.setString(3, endereco.getNumero());
        stmt.setString(4, endereco.getRua());
        stmt.setString(5, endereco.getCidade());
        stmt.setString(6, endereco.getEstado());
        stmt.setString(7, endereco.getPais());
        stmt.setString(8, endereco.getComplemento());
    }
}
