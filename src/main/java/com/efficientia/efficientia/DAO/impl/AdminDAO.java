package com.efficientia.efficientia.DAO.impl;
import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AdminModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class AdminDAO {
    //insert
    public boolean inserir(AdminModel adminModel) {
        if (adminModel == null) return false;
        String sql = """
                INSERT INTO admin(
                    email,
                    senha,
                    nome
                ) VALUES (?, ?, ?);
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
    //delete
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM admin WHERE id = ?;
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
    //select
    public List<AdminModel> listar() {
        String sql = """
                SELECT * FROM admin ORDER BY id;
                """;
        List<AdminModel> listaAdmin = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AdminModel adminModel = new AdminModel(
                        rs.getInt("id"),
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
    // Buscar por ID
    public AdminModel buscar(int id) {
        String sql = """
                SELECT * FROM admin WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AdminModel(
                            rs.getInt("id"),
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
    //update
    public boolean atualizar(AdminModel adminModel, int id) {
        if (adminModel == null) return false;
        String sql = """
                UPDATE admin SET
                    email = ?,
                    senha = ?,
                    nome = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            preencherStatement(stmt, adminModel);
            stmt.setInt(4, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Admin: " + e.getMessage());
            return false;
        }
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(PreparedStatement stmt, AdminModel adminModel) throws SQLException {
        stmt.setString(1, adminModel.getEmail());
        stmt.setString(2, adminModel.getSenha());
        stmt.setString(3, adminModel.getNome());
    }
}
