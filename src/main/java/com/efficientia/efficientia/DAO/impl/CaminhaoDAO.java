package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.CaminhaoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaminhaoDAO {

    // Inserir
    public boolean inserir(CaminhaoModel caminhaoModel) {
        String sql = """
                INSERT INTO caminhao (
                    placa_cavalo,
                    placa_carreta,
                    capacidade_maxima
                )
                VALUES (?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, caminhaoModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Caminhao: " + e.getMessage());
            return false;
        }
    }

    // Listar
    public List<CaminhaoModel> listar() {
        String sql = """
                SELECT * FROM caminhao ORDER BY id;
                """;

        List<CaminhaoModel> caminhaoModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CaminhaoModel caminhaoModel = new CaminhaoModel(
                        rs.getInt("id"),
                        rs.getString("placa_cavalo"),
                        rs.getString("placa_carreta"),
                        rs.getInt("capacidade_maxima")
                );

                caminhaoModels.add(caminhaoModel);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Caminhao: " + e.getMessage());
        }

        return caminhaoModels;
    }

    // Atualizar
    public boolean atualizar(CaminhaoModel caminhaoModel, int id) {
        String sql = """
                UPDATE caminhao
                SET placa_cavalo = ?,
                    placa_carreta = ?,
                    capacidade_maxima = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, caminhaoModel);
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Caminhao: " + e.getMessage());
            return false;
        }
    }

    // Excluir
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM caminhao WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir Caminhao: " + e.getMessage());
            return false;
        }
    }

    // Buscar por ID
    public CaminhaoModel buscar(int id) {
        String sql = """
                SELECT * FROM caminhao WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CaminhaoModel(
                            rs.getInt("id"),
                            rs.getString("placa_cavalo"),
                            rs.getString("placa_carreta"),
                            rs.getInt("capacidade_maxima")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Caminhao: " + e.getMessage());
        }

        return null;
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(
            PreparedStatement stmt,
            CaminhaoModel caminhaoModel
    ) throws SQLException {
        stmt.setString(1, caminhaoModel.getPlacaCavalo());
        stmt.setString(2, caminhaoModel.getPlacaCarreta());
        stmt.setInt(3, caminhaoModel.getCapacidadeMaxima());
    }
}
