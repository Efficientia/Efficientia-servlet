package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AdminModel;
import com.efficientia.efficientia.model.EmpresaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Administrador.
 *
 * Responsável por gerenciar as operações de persistência e consulta dos
 * administradores do sistema na tabela 'adm' do banco de dados relacional PostgreSQL,
 * realizando também a junção com a tabela 'empresa' através de ConnectionFactory
 * e blocos try-with-resources.
 */
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class AdminDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo administrador no banco de dados.
     *
     * @param adminModel objeto AdminModel contendo os dados do administrador a ser cadastrado
     * @return true se o registro foi inserido com sucesso, false caso ocorra falha ou o modelo seja nulo
     */
    public boolean inserir(AdminModel adminModel) {
        if (adminModel == null) return false;
        String sql = """
                INSERT INTO adm (
                    id_empresa,
                    email,
                    senha,
                    nome
                ) VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            preencherStatement(stmt, adminModel);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos os administradores cadastrados no banco de dados, com os dados da empresa vinculada, ordenados por ID.
     *
     * @return lista contendo os administradores encontrados ou lista vazia em caso de falha/ausência de registros
     */
    public List<AdminModel> listar() {
        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM adm a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                ORDER BY a.id;
                """;
        List<AdminModel> listaAdmin = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
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

                AdminModel adminModel = new AdminModel(
                        rs.getInt("id"),
                        empresaModel,
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("nome")
                );
                listaAdmin.add(adminModel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Admin: " + e.getMessage());
        }
        return listaAdmin;
    }

    /**
     * Atualiza os dados de um administrador existente com base em seu ID.
     *
     * @param adminModel objeto contendo os novos dados do administrador
     * @param id         identificador numérico do administrador a ser modificado
     * @return true se o registro foi atualizado com sucesso, false caso ocorra falha ou o modelo seja nulo
     */
    public boolean atualizar(AdminModel adminModel, int id) {
        if (adminModel == null) return false;
        String sql = """
                UPDATE adm SET
                    id_empresa = ?,
                    email = ?,
                    senha = ?,
                    nome = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            preencherStatement(stmt, adminModel);
            stmt.setInt(5, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um registro de administrador do banco de dados pelo seu ID.
     *
     * @param id identificador único do administrador a ser excluído
     * @return true se a exclusão for efetuada com sucesso, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM adm WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir Admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza um administrador pelo seu identificador único, trazendo os dados da empresa vinculada.
     *
     * @param id identificador único do administrador
     * @return objeto AdminModel se encontrado, ou null caso contrário
     */
    public AdminModel buscar(int id) {
        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM adm a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE a.id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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

                    return new AdminModel(
                            rs.getInt("id"),
                            empresaModel,
                            rs.getString("email"),
                            rs.getString("senha"),
                            rs.getString("nome")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Admin: " + e.getMessage());
        }
        return null;
    }

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere um administrador de forma simplificada com os campos essenciais.
     */
    public boolean inserirSimples(String nome, String email, String senha, Integer idEmpresa) {
        String sql = """
                INSERT INTO adm (id_empresa, email, senha, nome)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idEmpresa != null && idEmpresa > 0) {
                stmt.setInt(1, idEmpresa);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, nome);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir administrador simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o nome de um administrador.
     */
    public boolean atualizarNome(int id, String novoNome) {
        String sql = "UPDATE adm SET nome = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoNome != null ? novoNome.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar nome do administrador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o e-mail de um administrador.
     */
    public boolean atualizarEmail(int id, String novoEmail) {
        String sql = "UPDATE adm SET email = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoEmail != null ? novoEmail.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar email do administrador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a senha de um administrador.
     */
    public boolean atualizarSenha(int id, String novaSenha) {
        String sql = "UPDATE adm SET senha = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaSenha);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar senha do administrador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a empresa vinculada a um administrador.
     */
    public boolean atualizarEmpresa(int id, Integer idEmpresa) {
        String sql = "UPDATE adm SET id_empresa = ? WHERE id = ?;";
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
            System.out.println("Erro ao atualizar empresa do administrador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca administradores por nome, suportando correspondência exata, parcial ("picada")
     * e case-insensitive (ignorando maiúsculas e minúsculas), trazendo os dados da empresa via LEFT JOIN.
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de administradores encontrados
     */
    public List<AdminModel> buscarPorNome(String termo) {
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
                FROM adm a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE 1=1
                """);

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(a.nome) LIKE ?");
        }
        sql.append(" ORDER BY a.nome ASC, a.id ASC;");

        List<AdminModel> listaAdmin = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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

                    AdminModel adminModel = new AdminModel(
                            rs.getInt("id"),
                            empresaModel,
                            rs.getString("email"),
                            rs.getString("senha"),
                            rs.getString("nome")
                    );
                    listaAdmin.add(adminModel);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Admin por nome: " + e.getMessage());
        }

        return listaAdmin;
    }

    /**
     * Busca administradores pelo e-mail, ignorando maiúsculas e minúsculas.
     *
     * @param email endereço de email a pesquisar
     * @return lista de administradores encontrados
     */
    public List<AdminModel> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return listar();
        }

        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM adm a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE LOWER(a.email) LIKE ?
                ORDER BY a.nome ASC, a.id ASC;
                """;

        List<AdminModel> listaAdmin = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + email.trim().toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaAdmin.add(extrairAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Admin por email: " + e.getMessage());
        }

        return listaAdmin;
    }

    /**
     * Busca administradores vinculados a uma empresa específica.
     *
     * @param idEmpresa identificador da empresa
     * @return lista de administradores da empresa
     */
    public List<AdminModel> buscarPorEmpresa(int idEmpresa) {
        String sql = """
                SELECT a.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM adm a
                LEFT JOIN empresa e ON e.id = a.id_empresa
                WHERE a.id_empresa = ?
                ORDER BY a.nome ASC, a.id ASC;
                """;

        List<AdminModel> listaAdmin = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idEmpresa);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaAdmin.add(extrairAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Admin por empresa: " + e.getMessage());
        }

        return listaAdmin;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Extrai os dados do ResultSet hidratando o objeto AdminModel com a empresa associada.
     */
    private AdminModel extrairAdmin(ResultSet rs) throws SQLException {
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

        return new AdminModel(
                rs.getInt("id"),
                empresaModel,
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("nome")
        );
    }

    /**
     * Mapeia os atributos do modelo AdminModel para os parâmetros do PreparedStatement.
     *
     * @param stmt       PreparedStatement configurado com a query SQL
     * @param adminModel objeto contendo os dados do administrador
     * @throws SQLException se ocorrer erro durante a parametrização
     */
    private void preencherStatement(PreparedStatement stmt, AdminModel adminModel) throws SQLException {
        if (adminModel.getEmpresaModel() != null && adminModel.getEmpresaModel().getId() > 0) {
            stmt.setInt(1, adminModel.getEmpresaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }
        stmt.setString(2, adminModel.getEmail());
        stmt.setString(3, adminModel.getSenha());
        stmt.setString(4, adminModel.getNome());
    }
}
