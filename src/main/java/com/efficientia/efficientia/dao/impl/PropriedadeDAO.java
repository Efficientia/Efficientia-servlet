package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EnderecoModel;
import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.model.PropriedadeModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Propriedade Rural.
 *
 * Centraliza as operações de persistência e recuperação da tabela 'propriedade',
 * realizando junções (JOIN) com as tabelas 'pecuarista' e 'endereco' para
 * reconstituir o grafo de objetos da propriedade rural e seus vínculos.
 */
public class PropriedadeDAO {

    private static final String BASE_SELECT = """
            SELECT
                pr.id AS propriedade_id,
                pr.nome AS propriedade_nome,

                -- Pecuarista
                pec.id AS pecuarista_id,
                pec.cpf AS pecuarista_cpf,
                pec.assinatura AS pecuarista_assinatura,
                pec.data_nascimento AS pecuarista_data_nascimento,
                pec.nome AS pecuarista_nome,
                pec.senha AS pecuarista_senha,
                pec.email AS pecuarista_email,
                pec.telefone AS pecuarista_telefone,

                -- Endereço
                e.id AS endereco_id,
                e.cep AS endereco_cep,
                e.tipo AS endereco_tipo,
                e.numero AS endereco_numero,
                e.rua AS endereco_rua,
                e.cidade AS endereco_cidade,
                e.estado AS endereco_estado,
                e.pais AS endereco_pais,
                e.complemento AS endereco_complemento

            FROM propriedade pr
            JOIN pecuarista pec ON pec.id = pr.id_pecuarista
            JOIN endereco e ON e.id = pr.id_endereco
            """;

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere uma nova propriedade rural vinculada a um pecuarista e a um endereço.
     *
     * @param propriedadeModel objeto contendo os dados da propriedade e referências das entidades vinculadas
     * @return true se a propriedade for gravada com sucesso, false em caso de erro
     */
    public boolean inserir(PropriedadeModel propriedadeModel) {
        String sql = """
                INSERT INTO propriedade (id_pecuarista, id_endereco, nome)
                VALUES (?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, propriedadeModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Propriedade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todas as propriedades cadastradas, montando os objetos associados de Pecuarista e Endereço.
     *
     * @return lista contendo as propriedades rurais completas ordenadas por ID
     */
    public List<PropriedadeModel> listar() {
        String sql = BASE_SELECT + " ORDER BY pr.id;";
        List<PropriedadeModel> propriedades = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                propriedades.add(extrairPropriedade(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Propriedade: " + e.getMessage());
        }

        return propriedades;
    }

    /**
     * Localiza uma propriedade pelo ID, reconstituindo o pecuarista e o endereço correspondentes via JOIN.
     *
     * @param id identificador único da propriedade
     * @return objeto PropriedadeModel completamente hidratado ou null se não for encontrada
     */
    public PropriedadeModel buscar(int id) {
        String sql = BASE_SELECT + " WHERE pr.id = ?;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPropriedade(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Propriedade: " + e.getMessage());
        }

        return null;
    }

    /**
     * Atualiza as informações e vínculos de uma propriedade rural existente.
     *
     * @param propriedadeModel objeto com os novos dados e chaves estrangeiras
     * @param id               identificador único da propriedade a ser atualizada
     * @return true se a atualização foi efetuada com sucesso, false caso contrário
     */
    public boolean atualizar(PropriedadeModel propriedadeModel, int id) {
        String sql = """
                UPDATE propriedade
                SET id_pecuarista = ?,
                    id_endereco = ?,
                    nome = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, propriedadeModel);
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Propriedade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um registro de propriedade rural da base de dados.
     *
     * @param id identificador único da propriedade a ser removida
     * @return true se o registro foi deletado, false caso ocorra falha
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM propriedade
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir Propriedade: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca propriedades por nome, suportando correspondência exata, parcial ("picada")
     * e case-insensitive (ignorando maiúsculas e minúsculas).
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de propriedades encontradas com pecuarista e endereço associados
     */
    public List<PropriedadeModel> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }

        String[] tokens = termo.trim().split("\\s+");
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append(" WHERE 1=1");

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(pr.nome) LIKE ?");
        }
        sql.append(" ORDER BY pr.nome ASC, pr.id ASC;");

        List<PropriedadeModel> propriedades = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    propriedades.add(extrairPropriedade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Propriedades por nome: " + e.getMessage());
        }

        return propriedades;
    }

    /**
     * Busca todas as propriedades rurais pertencentes a um determinado pecuarista.
     *
     * @param idPecuarista identificador do pecuarista proprietário
     * @return lista de propriedades pertencentes ao pecuarista
     */
    public List<PropriedadeModel> buscarPorPecuarista(int idPecuarista) {
        String sql = BASE_SELECT + " WHERE pr.id_pecuarista = ? ORDER BY pr.nome ASC, pr.id ASC;";
        List<PropriedadeModel> propriedades = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idPecuarista);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    propriedades.add(extrairPropriedade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar propriedades por pecuarista: " + e.getMessage());
        }

        return propriedades;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Extrai os dados do ResultSet e hidrata um objeto PropriedadeModel completo
     * com seu PecuaristaModel e EnderecoModel associados.
     */
    private PropriedadeModel extrairPropriedade(ResultSet rs) throws SQLException {
        Date dataNascPec = rs.getDate("pecuarista_data_nascimento");
        LocalDate dataNascimento = dataNascPec != null ? dataNascPec.toLocalDate() : null;

        PecuaristaModel pecuarista = new PecuaristaModel(
                rs.getInt("pecuarista_id"),
                rs.getString("pecuarista_cpf"),
                rs.getString("pecuarista_assinatura"),
                dataNascimento,
                rs.getString("pecuarista_nome"),
                rs.getString("pecuarista_senha"),
                rs.getString("pecuarista_email"),
                rs.getString("pecuarista_telefone")
        );

        EnderecoModel endereco = new EnderecoModel(
                rs.getInt("endereco_id"),
                rs.getString("endereco_cep"),
                rs.getString("endereco_tipo"),
                rs.getString("endereco_numero"),
                rs.getString("endereco_rua"),
                rs.getString("endereco_cidade"),
                rs.getString("endereco_estado"),
                rs.getString("endereco_pais"),
                rs.getString("endereco_complemento")
        );

        return new PropriedadeModel(
                rs.getInt("propriedade_id"),
                pecuarista,
                endereco,
                rs.getString("propriedade_nome")
        );
    }

    /**
     * Preenche os parâmetros do PreparedStatement tratando referências nulas de objetos compostos.
     *
     * @param stmt             PreparedStatement pronto para recepção dos parâmetros
     * @param propriedadeModel modelo contendo os dados da propriedade
     * @throws SQLException se ocorrer erro na associação de tipos
     */
    private void preencherStatement(PreparedStatement stmt, PropriedadeModel propriedadeModel) throws SQLException {
        if (propriedadeModel.getPecuaristaModel() != null) {
            stmt.setInt(1, propriedadeModel.getPecuaristaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        if (propriedadeModel.getEnderecoModel() != null) {
            stmt.setInt(2, propriedadeModel.getEnderecoModel().getId());
        } else {
            stmt.setNull(2, Types.INTEGER);
        }

        stmt.setString(3, propriedadeModel.getNome());
    }
}
