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
    public void insertAnalista(AnalistaModel analistaModel) throws SQLException {
        if (analistaModel == null) return;

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

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete
    public void deleteAnalista(AnalistaModel analistaModel) throws SQLException {
        if (analistaModel != null) {
            deleteAnalista(analistaModel.getId());
        }
    }

    public void deleteAnalista(int id) throws SQLException {
        String sql = """
                DELETE FROM analista WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //select
    public List<AnalistaModel> listar() throws SQLException {
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
            e.printStackTrace();
        }
        return listaAnalista;
    }

    // Buscar por ID
    public AnalistaModel buscarPorId(int id) throws SQLException {
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
            e.printStackTrace();
        }
        return null;
    }

    public AnalistaModel buscar(int id) throws SQLException {
        return buscarPorId(id);
    }

    //update
    public void updateAnalista(AnalistaModel analistaModel) throws SQLException {
        if (analistaModel == null) return;

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

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}