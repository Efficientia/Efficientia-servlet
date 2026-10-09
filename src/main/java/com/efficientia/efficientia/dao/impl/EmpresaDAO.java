package com.efficientia.efficientia.dao.impl;

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
                empresas.add(extrairEmpresa(rs));
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
     * Remove uma empresa da base de dados através de seu ID.
     *
     * @param id identificador numérico da empresa a ser excluída
     * @return true se o registro foi removido com sucesso, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM empresa
                WHERE id = ?;
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
     * Localiza uma empresa pelo seu identificador único no banco de dados.
     *
     * @param id identificador numérico da empresa
     * @return objeto EmpresaModel se encontrado, ou null caso contrário
     */
    public EmpresaModel buscar(int id) {
        String sql = """
                SELECT * FROM empresa
                WHERE id = ?;
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairEmpresa(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresaModel: " + e.getMessage());
        }

        return null;
    }

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere uma empresa de forma simplificada com os campos principais.
     */
    public boolean inserirSimples(String nome, String cnpj, String codigo) {
        String sql = """
                INSERT INTO empresa (nome, cnpj, codigo)
                VALUES (?, ?, ?);
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, cnpj);
            stmt.setString(3, codigo != null && !codigo.isBlank() ? codigo.trim().toUpperCase() : null);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir empresa simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a razão social / nome da empresa.
     */
    public boolean atualizarNome(int id, String novoNome) {
        String sql = "UPDATE empresa SET nome = ? WHERE id = ?;";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoNome != null ? novoNome.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar nome da empresa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o CNPJ da empresa.
     */
    public boolean atualizarCnpj(int id, String novoCnpj) {
        String sql = "UPDATE empresa SET cnpj = ? WHERE id = ?;";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoCnpj != null ? novoCnpj.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar CNPJ da empresa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o código identificador corporativo da empresa.
     */
    public boolean atualizarCodigo(int id, String novoCodigo) {
        String sql = "UPDATE empresa SET codigo = ? WHERE id = ?;";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoCodigo != null ? novoCodigo.trim().toUpperCase() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar código da empresa: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Localiza uma empresa pelo seu Cadastro Nacional da Pessoa Jurídica (CNPJ).
     *
     * @param cnpj número ou máscara de CNPJ
     * @return objeto EmpresaModel correspondente ou null caso não encontrado
     */
    public EmpresaModel buscarPorCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return null;
        }

        String cnpjLimpo = cnpj.replaceAll("\\D", "").trim();

        String sql = """
                SELECT * FROM empresa
                WHERE REGEXP_REPLACE(cnpj, '[^0-9]', '', 'g') = ?
                   OR cnpj = ?;
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpjLimpo);
            stmt.setString(2, cnpj.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairEmpresa(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresa por CNPJ: " + e.getMessage());
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
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        String sql = """
                SELECT * FROM empresa WHERE UPPER(codigo) = UPPER(?);
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairEmpresa(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresaModel por codigo: " + e.getMessage());
        }

        return null;
    }

    /**
     * Busca empresas pelo código corporativo (ex: EMP12345), suportando correspondência parcial
     * e ignorando maiúsculas e minúsculas.
     *
     * @param codigo termo ou código a ser buscado
     * @return lista de empresas encontradas
     */
    public List<EmpresaModel> buscarPorCodigoLista(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return listar();
        }

        String sql = """
                SELECT * FROM empresa
                WHERE UPPER(codigo) LIKE ?
                ORDER BY codigo ASC, id ASC;
                """;

        List<EmpresaModel> empresas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + codigo.trim().toUpperCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empresas.add(extrairEmpresa(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresas por codigo: " + e.getMessage());
        }

        return empresas;
    }

    /**
     * Busca empresas por nome, suportando correspondência exata, parcial e case-insensitive.
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de empresas encontradas
     */
    public List<EmpresaModel> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }

        String[] tokens = termo.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM empresa
                WHERE 1=1
                """);

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(nome) LIKE ?");
        }
        sql.append(" ORDER BY nome ASC, id ASC;");

        List<EmpresaModel> empresas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empresas.add(extrairEmpresa(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresas por nome: " + e.getMessage());
        }

        return empresas;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Constrói uma instância de EmpresaModel a partir do ResultSet atual.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto EmpresaModel hidratado
     * @throws SQLException se ocorrer erro de leitura do ResultSet
     */
    private EmpresaModel extrairEmpresa(ResultSet rs) throws SQLException {
        return new EmpresaModel(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cnpj"),
                rs.getString("codigo")
        );
    }

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
