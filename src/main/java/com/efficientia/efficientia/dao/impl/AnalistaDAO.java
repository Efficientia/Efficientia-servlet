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
                listaAnalista.add(extrairAnalista(rs));
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
                    return extrairAnalista(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista: " + e.getMessage());
        }
        return null;
    }

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere um analista de forma simplificada com os campos essenciais.
     */
    public boolean inserirSimples(String nome, String email, String senha, Integer idEmpresa) {
        String sql = """
                INSERT INTO analista (id_empresa, nome, email, senha)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idEmpresa != null && idEmpresa > 0) {
                stmt.setInt(1, idEmpresa);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, nome);
            stmt.setString(3, email);
            stmt.setString(4, senha);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir analista simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o nome de um analista.
     */
    public boolean atualizarNome(int id, String novoNome) {
        String sql = "UPDATE analista SET nome = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoNome != null ? novoNome.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar nome do analista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o e-mail de um analista.
     */
    public boolean atualizarEmail(int id, String novoEmail) {
        String sql = "UPDATE analista SET email = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoEmail != null ? novoEmail.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar email do analista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a senha de um analista.
     */
    public boolean atualizarSenha(int id, String novaSenha) {
        String sql = "UPDATE analista SET senha = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaSenha);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar senha do analista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a empresa vinculada a um analista.
     */
    public boolean atualizarEmpresa(int id, Integer idEmpresa) {
        String sql = "UPDATE analista SET id_empresa = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idEmpresa != null && idEmpresa > 0) {
                stmt.setInt(1, idEmpresa);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar empresa do analista: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca analistas por nome, suportando correspondência exata, parcial e case-insensitive.
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
                    listaAnalista.add(extrairAnalista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por nome: " + e.getMessage());
        }

        return listaAnalista;
    }

    /**
     * Busca analistas vinculados a uma empresa específica.
     *
     * @param idEmpresa ID da empresa associada
     * @return lista de analistas vinculados à empresa
     */
    public List<AnalistaModel> buscarPorEmpresa(int idEmpresa) {
        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE a.id_empresa = ?
                ORDER BY a.nome ASC, a.id ASC;
                """;

        List<AnalistaModel> listaAnalista = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idEmpresa);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaAnalista.add(extrairAnalista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por empresa: " + e.getMessage());
        }

        return listaAnalista;
    }

    /**
     * Localiza um analista pelo seu CPF.
     *
     * @param cpf número de CPF do analista
     * @return objeto AnalistaModel se encontrado, ou null caso contrário
     */
    public AnalistaModel buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE a.cpf = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, cpf.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairAnalista(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por CPF: " + e.getMessage());
        }

        return null;
    }

    /**
     * Localiza um analista pelo seu código funcional corporativo.
     *
     * @param codigo código de registro funcional
     * @return objeto AnalistaModel se encontrado, ou null caso contrário
     */
    public AnalistaModel buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;

        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE UPPER(a.codigo) = UPPER(?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, codigo.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairAnalista(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por código: " + e.getMessage());
        }

        return null;
    }

    /**
     * Localiza um analista pelo seu e-mail de acesso.
     *
     * @param email endereço de e-mail cadastrado
     * @return objeto AnalistaModel se encontrado, ou null caso contrário
     */
    public AnalistaModel buscarPorEmail(String email) {
        if (email == null || email.isBlank()) return null;

        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM analista a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE LOWER(a.email) = LOWER(?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairAnalista(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista por email: " + e.getMessage());
        }

        return null;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Constrói uma instância de AnalistaModel a partir do ResultSet atual com dados de Empresa.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto AnalistaModel populado
     * @throws SQLException se ocorrer erro de leitura do ResultSet
     */
    private AnalistaModel extrairAnalista(ResultSet rs) throws SQLException {
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
        if (analistaModel.getEmpresaModel() != null && analistaModel.getEmpresaModel().getId() > 0) {
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