package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EmpresaModel;
import com.efficientia.efficientia.model.MotoristaModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Motorista.
 *
 * Gerencia a persistência de condutores de veículos na tabela 'motorista',
 * incluindo o relacionamento opcional (LEFT JOIN) com a tabela 'empresa'
 * para carregar e associar os dados da transportadora parceira.
 */
public class MotoristaDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo motorista na base de dados.
     *
     * @param motoristaModel objeto contendo os atributos do motorista
     * @return true se a inclusão for bem-sucedida, false em caso de erro
     */
    public boolean inserir(MotoristaModel motoristaModel) {
        String sql = """
                INSERT INTO motorista (
                    id_empresa,
                    nome,
                    data_nascimento,
                    senha,
                    email,
                    telefone
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, motoristaModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Motorista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos os motoristas cadastrados, trazendo também os dados da empresa associada via LEFT JOIN.
     *
     * @return lista com os motoristas ordenados crescentemente por ID
     */
    public List<MotoristaModel> listar() {
        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                ORDER BY m.id;
                """;

        List<MotoristaModel> motoristaModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                motoristaModels.add(extrairMotorista(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Motorista: " + e.getMessage());
        }

        return motoristaModels;
    }

    /**
     * Atualiza os dados de um motorista existente a partir do seu ID.
     *
     * @param motoristaModel objeto com as alterações desejadas
     * @param id             identificador único do motorista a ser modificado
     * @return true se o registro foi atualizado, false em caso de falha
     */
    public boolean atualizar(MotoristaModel motoristaModel, int id) {
        String sql = """
                UPDATE motorista
                SET id_empresa = ?,
                    nome = ?,
                    data_nascimento = ?,
                    senha = ?,
                    email = ?,
                    telefone = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, motoristaModel);
            stmt.setInt(7, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Motorista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um motorista cadastrado a partir de seu ID.
     *
     * @param id identificador único do motorista a ser removido
     * @return true se a exclusão for efetuada, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM motorista WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir Motorista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza um motorista específico pelo ID, carregando os dados da empresa vinculada via LEFT JOIN.
     *
     * @param id identificador único do motorista
     * @return objeto MotoristaModel completo ou null se não encontrado
     */
    public MotoristaModel buscar(int id) {
        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                WHERE m.id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairMotorista(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Motorista: " + e.getMessage());
        }

        return null;
    }

    /**
     * Busca motoristas por nome, suportando correspondência exata, parcial ("picada")
     * e case-insensitive (ignorando maiúsculas e minúsculas), trazendo os dados da empresa via LEFT JOIN.
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de motoristas encontrados
     */
    public List<MotoristaModel> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }

        String[] tokens = termo.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("""
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                WHERE 1=1
                """);

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(m.nome) LIKE ?");
        }
        sql.append(" ORDER BY m.nome ASC, m.id ASC;");

        List<MotoristaModel> motoristaModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    motoristaModels.add(extrairMotorista(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar motoristas por nome: " + e.getMessage());
        }

        return motoristaModels;
    }

    /**
     * Busca motoristas pelo e-mail (ou parte do e-mail), ignorando maiúsculas e minúsculas.
     *
     * @param email termo de e-mail a pesquisar
     * @return lista de motoristas encontrados
     */
    public List<MotoristaModel> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return listar();
        }

        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                WHERE LOWER(m.email) LIKE ?
                ORDER BY m.nome ASC, m.id ASC;
                """;

        List<MotoristaModel> motoristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + email.trim().toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    motoristas.add(extrairMotorista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar motoristas por email: " + e.getMessage());
        }

        return motoristas;
    }

    /**
     * Busca motoristas pelo número de telefone (tolerante a caracteres especiais).
     *
     * @param telefone número ou fragmento do telefone
     * @return lista de motoristas encontrados
     */
    public List<MotoristaModel> buscarPorTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return listar();
        }

        String digitos = telefone.replaceAll("\\D", "");
        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                WHERE regexp_replace(m.telefone, '\\D', '', 'g') LIKE ?
                ORDER BY m.nome ASC, m.id ASC;
                """;

        List<MotoristaModel> motoristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + (digitos.isEmpty() ? telefone.trim() : digitos) + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    motoristas.add(extrairMotorista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar motoristas por telefone: " + e.getMessage());
        }

        return motoristas;
    }

    /**
     * Busca motoristas vinculados a uma empresa específica.
     *
     * @param idEmpresa identificador da empresa
     * @return lista de motoristas da empresa
     */
    public List<MotoristaModel> buscarPorEmpresa(int idEmpresa) {
        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                WHERE m.id_empresa = ?
                ORDER BY m.nome ASC, m.id ASC;
                """;

        List<MotoristaModel> motoristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idEmpresa);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    motoristas.add(extrairMotorista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar motoristas por empresa: " + e.getMessage());
        }

        return motoristas;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Extrai os dados do ResultSet hidratando o objeto MotoristaModel com sua empresa associada.
     */
    private MotoristaModel extrairMotorista(ResultSet rs) throws SQLException {
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

        Date dataNascimento = rs.getDate("data_nascimento");

        return new MotoristaModel(
                rs.getInt("id"),
                empresaModel,
                rs.getString("nome"),
                rs.getString("assinatura"),
                dataNascimento != null ? dataNascimento.toLocalDate() : null,
                rs.getString("senha"),
                rs.getString("email"),
                rs.getString("telefone")
        );
    }

    /**
     * Preenche os parâmetros do PreparedStatement tratando valores nulos para chave estrangeira e data.
     *
     * @param stmt           statement preparado para receber os parâmetros JDBC
     * @param motoristaModel modelo contendo os dados a serem vinculados
     * @throws SQLException em caso de falha no mapeamento de tipos
     */
    private void preencherStatement(
            PreparedStatement stmt,
            MotoristaModel motoristaModel
    ) throws SQLException {

        if (motoristaModel.getEmpresaModel() != null) {
            stmt.setInt(1, motoristaModel.getEmpresaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        stmt.setString(2, motoristaModel.getNome());

        if (motoristaModel.getDataNascimento() != null) {
            stmt.setDate(
                    3,
                    Date.valueOf(motoristaModel.getDataNascimento())
            );
        } else {
            stmt.setNull(3, Types.DATE);
        }

        stmt.setString(4, motoristaModel.getSenha());
        stmt.setString(5, motoristaModel.getEmail());
        stmt.setString(6, motoristaModel.getTelefone());
    }
}
