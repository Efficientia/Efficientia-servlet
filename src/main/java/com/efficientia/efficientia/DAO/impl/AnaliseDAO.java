package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AnaliseModel;
import com.efficientia.efficientia.model.AnalistaModel;
import com.efficientia.efficientia.model.TrajetoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class AnaliseDAO {

    // Insert
    public void insertAnalise(AnaliseModel analiseModel) throws SQLException {
        if (analiseModel == null) return;

        String sql = """
                INSERT INTO analise(
                    id_analista,
                    id_trajeto,
                    data_analise,
                    status_analise,
                    observacao
                ) VALUES (?, ?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, analiseModel.getAnalista() != null ? analiseModel.getAnalista().getId() : 0);
            stmt.setInt(2, analiseModel.getTrajeto() != null ? analiseModel.getTrajeto().getId() : 0);
            stmt.setTimestamp(3, analiseModel.getDataAnalise() != null ? Timestamp.valueOf(analiseModel.getDataAnalise()) : null);
            stmt.setString(4, analiseModel.getStatusAnalise());
            stmt.setString(5, analiseModel.getObservacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteAnalise(int id) throws SQLException {
        String sql = """
                DELETE FROM analise WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para FKs (Analista e Trajeto)
    public AnalistaModel obterAnalista(int id) {
        if (id <= 0) {
            return null;
        }
        try {
            AnalistaDAO analistaDAO = new AnalistaDAO();
            AnalistaModel analista = analistaDAO.buscarPorId(id);
            if (analista != null) {
                return analista;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AnalistaModel(id, null, null, null, null, null, null, null, null);
    }

    public TrajetoModel obterTrajeto(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT * FROM trajeto WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp dataHoraInicio = rs.getTimestamp("data_hora_inicio");
                    Timestamp dataHoraFim = rs.getTimestamp("data_hora_fim");
                    Timestamp horarioEmbarque = rs.getTimestamp("horario_embarque");
                    Timestamp horarioDesembarque = rs.getTimestamp("horario_desembarque");

                    return new TrajetoModel(
                            rs.getInt("id"),
                            null,
                            null,
                            rs.getString("status"),
                            dataHoraInicio != null ? dataHoraInicio.toLocalDateTime() : null,
                            dataHoraFim != null ? dataHoraFim.toLocalDateTime() : null,
                            rs.getInt("km_saida"),
                            rs.getInt("km_chegada"),
                            rs.getString("nome_pecuarista"),
                            rs.getString("numero_gta"),
                            rs.getString("numero_nota_fiscal"),
                            horarioEmbarque != null ? horarioEmbarque.toLocalDateTime() : null,
                            rs.getInt("qtd_macho"),
                            rs.getInt("qtd_femea"),
                            rs.getInt("qtd_marruco"),
                            horarioDesembarque != null ? horarioDesembarque.toLocalDateTime() : null,
                            rs.getString("numero_curral"),
                            rs.getString("nome_curraleiro"),
                            rs.getString("nome_manobrista")
                    );
                }
            }
        } catch (Exception e) {
            // Em caso de falha ou ausência do registro, instancia o modelo existente com o id
        }

        return new TrajetoModel(id, null, null, null, null, null, null, null, null, null, null, null, 0, 0, 0, null, null, null, null);
    }

    // Select - Listar todos
    public List<AnaliseModel> listar() throws SQLException {
        String sql = """
                SELECT * FROM analise ORDER BY id;
                """;
        List<AnaliseModel> listaAnalise = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idAnalista = rs.getInt("id_analista");
                int idTrajeto = rs.getInt("id_trajeto");
                Timestamp dataAnaliseTimestamp = rs.getTimestamp("data_analise");

                AnaliseModel analiseModel = new AnaliseModel(
                        rs.getInt("id"),
                        obterAnalista(idAnalista),
                        obterTrajeto(idTrajeto),
                        dataAnaliseTimestamp != null ? dataAnaliseTimestamp.toLocalDateTime() : null,
                        rs.getString("status_analise"),
                        rs.getString("observacao")
                );
                listaAnalise.add(analiseModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaAnalise;
    }

    // Select por ID
    public AnaliseModel buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT * FROM analise WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idAnalista = rs.getInt("id_analista");
                    int idTrajeto = rs.getInt("id_trajeto");
                    Timestamp dataAnaliseTimestamp = rs.getTimestamp("data_analise");
                    return new AnaliseModel(
                            rs.getInt("id"),
                            obterAnalista(idAnalista),
                            obterTrajeto(idTrajeto),
                            dataAnaliseTimestamp != null ? dataAnaliseTimestamp.toLocalDateTime() : null,
                            rs.getString("status_analise"),
                            rs.getString("observacao")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AnaliseModel buscar(int id) throws SQLException {
        return buscarPorId(id);
    }

    public AnaliseModel selectPorId(int id) throws SQLException {
        return buscarPorId(id);
    }

    // Update
    public void updateAnalise(AnaliseModel analiseModel) throws SQLException {
        if (analiseModel == null) return;

        String sql = """
                UPDATE analise SET
                    id_analista = ?,
                    id_trajeto = ?,
                    data_analise = ?,
                    status_analise = ?,
                    observacao = ?
                WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, analiseModel.getAnalista() != null ? analiseModel.getAnalista().getId() : 0);
            stmt.setInt(2, analiseModel.getTrajeto() != null ? analiseModel.getTrajeto().getId() : 0);
            stmt.setTimestamp(3, analiseModel.getDataAnalise() != null ? Timestamp.valueOf(analiseModel.getDataAnalise()) : null);
            stmt.setString(4, analiseModel.getStatusAnalise());
            stmt.setString(5, analiseModel.getObservacao());
            stmt.setInt(6, analiseModel.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}