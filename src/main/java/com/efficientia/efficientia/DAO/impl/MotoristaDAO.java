package com.efficientia.efficientia.DAO.impl;

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

public class MotoristaDAO {

    // Inserir
    public boolean inserir(MotoristaModel motoristaModel) {
        String sql = """
                INSERT INTO motorista (
                    id_empresa,
                    assinatura,
                    nome,
                    data_nascimento,
                    senha,
                    email,
                    telefone
                )
                VALUES (?, ?, ?, ?, ?, ?, ?);
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

    // Listar
    public List<MotoristaModel> listar() {
        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                ORDER BY m.id;
                """;

        List<MotoristaModel> motoristaModels = new ArrayList<>();

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
                            rs.getString("empresa_cnpj")
                    );
                }

                Date dataNascimento = rs.getDate("data_nascimento");

                MotoristaModel motoristaModel = new MotoristaModel(
                        rs.getInt("id"),
                        empresaModel,
                        rs.getString("nome"),
                        rs.getString("assinatura"),
                        dataNascimento != null
                                ? dataNascimento.toLocalDate()
                                : null,
                        rs.getString("senha"),
                        rs.getString("email"),
                        rs.getString("telefone")
                );

                motoristaModels.add(motoristaModel);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Motorista: " + e.getMessage());
        }

        return motoristaModels;
    }

    // Atualizar
    public boolean atualizar(MotoristaModel motoristaModel, int id) {
        String sql = """
                UPDATE motorista
                SET id_empresa = ?,
                    assinatura = ?,
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
            stmt.setInt(8, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Motorista: " + e.getMessage());
            return false;
        }
    }

    // Excluir
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

    // Buscar por ID
    public MotoristaModel buscar(int id) {
        String sql = """
                SELECT m.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj
                FROM motorista m
                LEFT JOIN empresa e ON e.id = m.id_empresa
                WHERE m.id = ?;
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
                                rs.getString("empresa_cnpj")
                        );
                    }

                    Date dataNascimento = rs.getDate("data_nascimento");

                    return new MotoristaModel(
                            rs.getInt("id"),
                            empresaModel,
                            rs.getString("nome"),
                            rs.getString("assinatura"),
                            dataNascimento != null
                                    ? dataNascimento.toLocalDate()
                                    : null,
                            rs.getString("senha"),
                            rs.getString("email"),
                            rs.getString("telefone")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Motorista: " + e.getMessage());
        }

        return null;
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(
            PreparedStatement stmt,
            MotoristaModel motoristaModel
    ) throws SQLException {

        if (motoristaModel.getEmpresaModel() != null) {
            stmt.setInt(1, motoristaModel.getEmpresaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        stmt.setString(2, motoristaModel.getAssinatura());
        stmt.setString(3, motoristaModel.getNome());

        if (motoristaModel.getDataNascimento() != null) {
            stmt.setDate(
                    4,
                    Date.valueOf(motoristaModel.getDataNascimento())
            );
        } else {
            stmt.setNull(4, Types.DATE);
        }

        stmt.setString(5, motoristaModel.getSenha());
        stmt.setString(6, motoristaModel.getEmail());
        stmt.setString(7, motoristaModel.getTelefone());
    }
}
