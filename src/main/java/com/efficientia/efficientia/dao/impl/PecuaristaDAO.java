package com.efficientia.efficientia.dao.impl;

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
                                      data_nascimento,
                                      nome,
                                      senha,
                                      email,
                                      telefone)
                VALUES (?, ?, ?, ?, ?, ?);
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
                listaPecuarista.add(extrairPecuarista(rs));
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
                UPDATE pecuarista SET
                    cpf = ?,
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
            stmt.setInt(7, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um pecuarista da base de dados através do seu ID.
     *
     * @param id identificador único do pecuarista a ser removido
     * @return true se o registro foi removido com sucesso, false em caso de falha
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM pecuarista
                WHERE id = ?;
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
     * Localiza um pecuarista com base em seu ID primário.
     *
     * @param id identificador numérico do pecuarista
     * @return objeto PecuaristaModel populado ou null caso não seja localizado
     */
    public PecuaristaModel buscar(int id) {
        String sql = """
                SELECT * FROM pecuarista
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPecuarista(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuarista: " + e.getMessage());
        }

        return null;
    }

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere um pecuarista de forma simplificada apenas com os dados essenciais.
     */
    public boolean inserirSimples(String nome, String cpf, String email, String senha) {
        String sql = """
                INSERT INTO pecuarista (nome, cpf, email, senha)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, email);
            stmt.setString(4, senha);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir pecuarista simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o CPF de um pecuarista.
     */
    public boolean atualizarCpf(int id, String novoCpf) {
        String sql = "UPDATE pecuarista SET cpf = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoCpf != null ? novoCpf.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar CPF do pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o nome de um pecuarista.
     */
    public boolean atualizarNome(int id, String novoNome) {
        String sql = "UPDATE pecuarista SET nome = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoNome != null ? novoNome.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar nome do pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o e-mail de um pecuarista.
     */
    public boolean atualizarEmail(int id, String novoEmail) {
        String sql = "UPDATE pecuarista SET email = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoEmail != null ? novoEmail.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar email do pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o telefone de um pecuarista.
     */
    public boolean atualizarTelefone(int id, String novoTelefone) {
        String sql = "UPDATE pecuarista SET telefone = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoTelefone != null ? novoTelefone.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar telefone do pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a senha de acesso de um pecuarista.
     */
    public boolean atualizarSenha(int id, String novaSenha) {
        String sql = "UPDATE pecuarista SET senha = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaSenha);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar senha do pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a data de nascimento de um pecuarista.
     */
    public boolean atualizarDataNascimento(int id, LocalDate novaData) {
        String sql = "UPDATE pecuarista SET data_nascimento = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (novaData != null) {
                stmt.setDate(1, Date.valueOf(novaData));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar data de nascimento do pecuarista: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a assinatura de um pecuarista.
     */
    public boolean atualizarAssinatura(int id, String novaAssinatura) {
        String sql = "UPDATE pecuarista SET assinatura = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaAssinatura);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar assinatura do pecuarista: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca pecuaristas por nome, suportando correspondência exata, parcial e case-insensitive.
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de pecuaristas encontrados
     */
    public List<PecuaristaModel> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }

        String[] tokens = termo.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM pecuarista
                WHERE 1=1
                """);

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(nome) LIKE ?");
        }
        sql.append(" ORDER BY nome ASC, id ASC;");

        List<PecuaristaModel> pecuaristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pecuaristas.add(extrairPecuarista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuaristas por nome: " + e.getMessage());
        }

        return pecuaristas;
    }

    /**
     * Localiza pecuaristas por número de CPF.
     *
     * @param cpf CPF numérico ou formatado
     * @return lista de pecuaristas correspondentes
     */
    public List<PecuaristaModel> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return listar();
        }

        String cpfLimpo = cpf.replaceAll("\\D", "").trim();

        String sql = """
                SELECT * FROM pecuarista
                WHERE REGEXP_REPLACE(cpf, '[^0-9]', '', 'g') LIKE ?
                   OR cpf LIKE ?
                ORDER BY nome ASC, id ASC;
                """;

        List<PecuaristaModel> pecuaristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + cpfLimpo + "%");
            stmt.setString(2, "%" + cpf.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pecuaristas.add(extrairPecuarista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuaristas por CPF: " + e.getMessage());
        }

        return pecuaristas;
    }

    /**
     * Localiza pecuaristas por e-mail de acesso.
     *
     * @param email endereço de e-mail cadastrado
     * @return lista de pecuaristas correspondentes
     */
    public List<PecuaristaModel> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return listar();
        }

        String sql = """
                SELECT * FROM pecuarista
                WHERE LOWER(email) LIKE ?
                ORDER BY nome ASC, id ASC;
                """;

        List<PecuaristaModel> pecuaristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + email.trim().toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pecuaristas.add(extrairPecuarista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuaristas por e-mail: " + e.getMessage());
        }

        return pecuaristas;
    }

    /**
     * Localiza pecuaristas por telefone de contato.
     *
     * @param telefone número de telefone formatado ou numérico
     * @return lista de pecuaristas correspondentes
     */
    public List<PecuaristaModel> buscarPorTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return listar();
        }

        String telefoneLimpo = telefone.replaceAll("\\D", "").trim();

        String sql = """
                SELECT * FROM pecuarista
                WHERE REGEXP_REPLACE(telefone, '[^0-9]', '', 'g') LIKE ?
                   OR telefone LIKE ?
                ORDER BY nome ASC, id ASC;
                """;

        List<PecuaristaModel> pecuaristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + telefoneLimpo + "%");
            stmt.setString(2, "%" + telefone.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pecuaristas.add(extrairPecuarista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuaristas por telefone: " + e.getMessage());
        }

        return pecuaristas;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Constrói uma instância de PecuaristaModel a partir do ResultSet atual.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto PecuaristaModel preenchido
     * @throws SQLException em caso de falha na leitura dos dados
     */
    private PecuaristaModel extrairPecuarista(ResultSet rs) throws SQLException {
        Date dataNascimento = rs.getDate("data_nascimento");

        return new PecuaristaModel(
                rs.getInt("id"),
                rs.getString("cpf"),
                rs.getString("assinatura"),
                dataNascimento != null ? dataNascimento.toLocalDate() : null,
                rs.getString("nome"),
                rs.getString("senha"),
                rs.getString("email"),
                rs.getString("telefone")
        );
    }

    /**
     * Preenche os parâmetros do PreparedStatement com os atributos de PecuaristaModel.
     *
     * @param stmt            PreparedStatement associado à query SQL
     * @param pecuaristaModel modelo com os dados a serem vinculados
     * @throws SQLException em caso de erro na vinculação dos parâmetros
     */
    private void preencherStatement(PreparedStatement stmt, PecuaristaModel pecuaristaModel) throws SQLException {
        stmt.setString(1, pecuaristaModel.getCpf());
        stmt.setDate(2, pecuaristaModel.getDataNascimento() != null ? Date.valueOf(pecuaristaModel.getDataNascimento()) : null);
        stmt.setString(3, pecuaristaModel.getNome());
        stmt.setString(4, pecuaristaModel.getSenha());
        stmt.setString(5, pecuaristaModel.getEmail());
        stmt.setString(6, pecuaristaModel.getTelefone());
    }
}
