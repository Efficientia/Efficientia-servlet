package com.efficientia.efficientia.dao.impl;

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
                enderecoModels.add(extrairEndereco(rs));
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
     * Exclui um endereço da base de dados através de seu identificador numérico.
     *
     * @param id identificador do registro a ser removido
     * @return true se o registro foi deletado, false caso ocorra falha
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
     * Consulta um endereço através de sua chave primária.
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
                    return extrairEndereco(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Endereco: " + e.getMessage());
            return null;
        }
    }

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere um endereço de forma simplificada com logradouro básico e CEP.
     */
    public boolean inserirSimples(String rua, int numero, String bairro, String cidade, String cep) {
        String sql = """
                INSERT INTO endereco (rua, numero, bairro, cidade, cep)
                VALUES (?, ?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rua);
            stmt.setInt(2, numero);
            stmt.setString(3, bairro);
            stmt.setString(4, cidade);
            stmt.setString(5, cep);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir endereço simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o CEP de um endereço.
     */
    public boolean atualizarCep(int id, String novoCep) {
        String sql = "UPDATE endereco SET cep = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoCep != null ? novoCep.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar CEP do endereço: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a cidade de um endereço.
     */
    public boolean atualizarCidade(int id, String novaCidade) {
        String sql = "UPDATE endereco SET cidade = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaCidade != null ? novaCidade.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cidade do endereço: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o bairro de um endereço.
     */
    public boolean atualizarBairro(int id, String novoBairro) {
        String sql = "UPDATE endereco SET bairro = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoBairro != null ? novoBairro.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar bairro do endereço: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a rua de um endereço.
     */
    public boolean atualizarRua(int id, String novaRua) {
        String sql = "UPDATE endereco SET rua = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaRua != null ? novaRua.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar rua do endereço: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o número do imóvel.
     */
    public boolean atualizarNumero(int id, int novoNumero) {
        String sql = "UPDATE endereco SET numero = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, novoNumero);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar número do endereço: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Localiza endereços com base no Código de Endereçamento Postal (CEP).
     *
     * @param cep código postal formatado ou numérico
     * @return lista de endereços que possuem o CEP informado
     */
    public List<EnderecoModel> buscarPorCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return listar();
        }

        String cepLimpo = cep.replaceAll("\\D", "").trim();

        String sql = """
                SELECT * FROM endereco
                WHERE REGEXP_REPLACE(cep, '[^0-9]', '', 'g') LIKE ?
                   OR cep LIKE ?
                ORDER BY id ASC;
                """;

        List<EnderecoModel> enderecos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + cepLimpo + "%");
            stmt.setString(2, "%" + cep.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enderecos.add(extrairEndereco(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Endereco por CEP: " + e.getMessage());
        }

        return enderecos;
    }

    /**
     * Localiza endereços por cidade, tolerante a maiúsculas e minúsculas.
     *
     * @param cidade nome da cidade/município
     * @return lista de endereços encontrados no município
     */
    public List<EnderecoModel> buscarPorCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return listar();
        }

        String sql = """
                SELECT * FROM endereco
                WHERE LOWER(cidade) LIKE ?
                ORDER BY cidade ASC, id ASC;
                """;

        List<EnderecoModel> enderecos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + cidade.trim().toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enderecos.add(extrairEndereco(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Endereco por cidade: " + e.getMessage());
        }

        return enderecos;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Constrói uma instância de EnderecoModel a partir do ResultSet atual.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto EnderecoModel preenchido
     * @throws SQLException em caso de falha na leitura dos dados
     */
    private EnderecoModel extrairEndereco(ResultSet rs) throws SQLException {
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
    }

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
