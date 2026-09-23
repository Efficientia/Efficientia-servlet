package com.efficientia.efficientia.DAO.impl;
import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AnalistaModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class AnalistaDAO {
    //insert
    public boolean inserir(AnalistaModel analistaModel) {
        if (analistaModel == null) return false;

        String sql = """
                INSERT INTO analista(
                    cpf,
                    nome,
                    assinatura,
                    data_nascimento,
                    senha,
                    email,
                    telefone,
                    codigo
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, analistaModel.getCpf());
            stmt.setString(2, analistaModel.getNome());
            stmt.setString(3, analistaModel.getAssinatura());
            stmt.setDate(4, analistaModel.getDataNascimento() != null ? Date.valueOf(analistaModel.getDataNascimento()) : null);
            stmt.setString(5, analistaModel.getSenha());
            stmt.setString(6, analistaModel.getEmail());
            stmt.setString(7, analistaModel.getTelefone());
            stmt.setString(8, analistaModel.getCodigo());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Analista: " + e.getMessage());
            return false;
        }
    }
    //delete
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
    //select
    public List<AnalistaModel> listar() {
        String sql = """
                SELECT * FROM analista ORDER BY id;
                """;
        List<AnalistaModel> listaAnalista = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Date dataNasc = rs.getDate("data_nascimento");
                AnalistaModel analistaModel = new AnalistaModel(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        rs.getString("assinatura"),
                        dataNasc != null ? dataNasc.toLocalDate() : null,
                        rs.getString("senha"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("codigo")
                );
                listaAnalista.add(analistaModel);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Analista: " + e.getMessage());
        }
        return listaAnalista;
    }

    // Buscar por ID
    public AnalistaModel buscar(int id) {
        String sql = """
                SELECT * FROM analista WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dataNasc = rs.getDate("data_nascimento");
                    return new AnalistaModel(
                            rs.getInt("id"),
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
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Analista: " + e.getMessage());
        }
        return null;
    }
    //update
    public boolean atualizar(AnalistaModel analistaModel) {
        if (analistaModel == null) return false;

        String sql = """
                UPDATE analista SET
                    cpf = ?,
                    nome = ?,
                    assinatura = ?,
                    data_nascimento = ?,
                    senha = ?,
                    email = ?,
                    telefone = ?,
                    codigo = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, analistaModel.getCpf());
            stmt.setString(2, analistaModel.getNome());
            stmt.setString(3, analistaModel.getAssinatura());
            stmt.setDate(4, analistaModel.getDataNascimento() != null ? Date.valueOf(analistaModel.getDataNascimento()) : null);
            stmt.setString(5, analistaModel.getSenha());
            stmt.setString(6, analistaModel.getEmail());
            stmt.setString(7, analistaModel.getTelefone());
            stmt.setString(8, analistaModel.getCodigo());
            stmt.setInt(9, analistaModel.getId());
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Analista: " + e.getMessage());
            return false;
        }
    }
}