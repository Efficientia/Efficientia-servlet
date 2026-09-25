package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EnderecoModel;
import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.model.PropriedadeModel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PropriedadeDAO {

    // Inserir
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

    // Listar
    public List<PropriedadeModel> listar() {
        String sql = """
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
                ORDER BY pr.id;
                """;

        List<PropriedadeModel> propriedades = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
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

                PropriedadeModel propriedade = new PropriedadeModel(
                        rs.getInt("propriedade_id"),
                        pecuarista,
                        endereco,
                        rs.getString("propriedade_nome")
                );

                propriedades.add(propriedade);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Propriedade: " + e.getMessage());
        }

        return propriedades;
    }

    // Buscar por ID
    public PropriedadeModel buscar(int id) {
        String sql = """
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
                WHERE pr.id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Propriedade: " + e.getMessage());
        }

        return null;
    }

    // Atualizar
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

    // Excluir
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

    // Mapeamento do PreparedStatement
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
