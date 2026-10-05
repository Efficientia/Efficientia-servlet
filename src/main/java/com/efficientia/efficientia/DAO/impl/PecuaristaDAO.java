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

    /**
     * Busca pecuaristas por nome, suportando correspondência exata, parcial ("picada")
     * e case-insensitive (ignorando maiúsculas e minúsculas).
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
                    Date dataNascimento = rs.getDate("data_nascimento");
                    LocalDate nascimento = dataNascimento != null ? dataNascimento.toLocalDate() : null;

                    PecuaristaModel pecuaristaModel = new PecuaristaModel(
                            rs.getInt("id"),
                            rs.getString("cpf"),
                            rs.getString("assinatura"),
                            nascimento,
                            rs.getString("nome"),
                            rs.getString("senha"),
                            rs.getString("email"),
                            rs.getString("telefone")
                    );

                    pecuaristas.add(pecuaristaModel);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pecuaristas por nome: " + e.getMessage());
        }

        return pecuaristas;
    }

    /**
     * Busca pecuaristas pelo CPF (aceita com ou sem máscara/pontuação).
     *
     * @param cpf documento a pesquisar
     * @return lista de pecuaristas encontrados
     */
    public List<PecuaristaModel> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return listar();
        }

        String digitos = cpf.replaceAll("\\D", "");
        String sql = """
                SELECT * FROM pecuarista
                WHERE regexp_replace(cpf, '\\D', '', 'g') LIKE ?
                ORDER BY nome ASC, id ASC;
                """;

        List<PecuaristaModel> pecuaristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + (digitos.isEmpty() ? cpf.trim() : digitos) + "%");

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
     * Busca pecuaristas pelo e-mail, ignorando maiúsculas e minúsculas.
     *
     * @param email endereço de email a pesquisar
     * @return lista de pecuaristas encontrados
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
            System.out.println("Erro ao buscar pecuaristas por email: " + e.getMessage());
        }

        return pecuaristas;
    }

    /**
     * Busca pecuaristas pelo telefone (tolerante a caracteres especiais de máscara).
     *
     * @param telefone número a ser pesquisado
     * @return lista de pecuaristas encontrados
     */
    public List<PecuaristaModel> buscarPorTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return listar();
        }

        String digitos = telefone.replaceAll("\\D", "");
        String sql = """
                SELECT * FROM pecuarista
                WHERE regexp_replace(telefone, '\\D', '', 'g') LIKE ?
                ORDER BY nome ASC, id ASC;
                """;

        List<PecuaristaModel> pecuaristas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + (digitos.isEmpty() ? telefone.trim() : digitos) + "%");

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
     * Extrai os dados do ResultSet hidratando o objeto PecuaristaModel.
     */
    private PecuaristaModel extrairPecuarista(ResultSet rs) throws SQLException {
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
    }

    /**
     * Atribui os campos de PecuaristaModel aos parâmetros indexados do PreparedStatement.
     *
     * @param stmt            PreparedStatement pronto para recepção dos parâmetros
     * @param pecuaristaModel modelo com os dados cadastrais
     * @throws SQLException se houver erro durante a associação dos tipos JDBC
     */
    private void preencherStatement(PreparedStatement stmt, PecuaristaModel pecuaristaModel) throws SQLException {
        stmt.setString(1, pecuaristaModel.getCpf());

        if (pecuaristaModel.getDataNascimento() != null) {
            stmt.setDate(2, Date.valueOf(pecuaristaModel.getDataNascimento()));
        } else {
            stmt.setDate(2, null);
        }

        stmt.setString(3, pecuaristaModel.getNome());
        stmt.setString(4, pecuaristaModel.getSenha());
        stmt.setString(5, pecuaristaModel.getEmail());
        stmt.setString(6, pecuaristaModel.getTelefone());
    }
}
