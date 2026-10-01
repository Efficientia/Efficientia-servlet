package com.efficientia.efficientia.DAO.impl;
import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.AnaliseModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class AnaliseDAO {
    //insert
    public boolean inserir(AnaliseModel analiseModel) {
        if (analiseModel == null) return false;
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
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Analise: " + e.getMessage());
            return false;
        }
    }
    //delete
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM analise WHERE id = ?;
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir Analise: " + e.getMessage());
            return false;
        }
    }
    //select
//    public List<AnaliseModel> listar() {
//        String sql = """
//                SELECT * FROM analise ORDER BY id;
//                """;
//        List<AnaliseModel> listaAnalise = new ArrayList<>();
//        AnalistaDAO analistaDAO = new AnalistaDAO();
//        try (Connection connection = ConnectionFactory.getConnection();
//             PreparedStatement stmt = connection.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                Timestamp dataAnaliseTimestamp = rs.getTimestamp("data_analise");
//                AnaliseModel analiseModel = new AnaliseModel(
//                        rs.getInt("id"),
//                        analistaDAO.buscar(rs.getInt("id_analista")),
//                        dataAnaliseTimestamp != null ? dataAnaliseTimestamp.toLocalDateTime() : null,
//                        rs.getString("status_analise"),
//                        rs.getString("observacao")
//                );
//                listaAnalise.add(analiseModel);
//            }
//        } catch (SQLException e) {
//            System.out.println("Erro ao listar Analise: " + e.getMessage());
//        }
//        return listaAnalise;
//    }
    // Buscar por ID
//    public AnaliseModel buscar(int id) {
//        String sql = """
//                SELECT * FROM analise WHERE id = ?;
//                """;
//        AnalistaDAO analistaDAO = new AnalistaDAO();
//        try (Connection connection = ConnectionFactory.getConnection();
//             PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    Timestamp dataAnaliseTimestamp = rs.getTimestamp("data_analise");
//                    return new AnaliseModel(
//                            rs.getInt("id"),
//                            analistaDAO.buscar(rs.getInt("id_analista")),
//                            dataAnaliseTimestamp != null ? dataAnaliseTimestamp.toLocalDateTime() : null,
//                            rs.getString("status_analise"),
//                            rs.getString("observacao")
//                    );
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("Erro ao buscar Analise: " + e.getMessage());
//        }
//        return null;
//    }
    //update
    public boolean atualizar(AnaliseModel analiseModel) {
        if (analiseModel == null) return false;
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
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Analise: " + e.getMessage());
            return false;
        }
    }
}
// acho que precisa de um DAO de trajeto para o Analise (ligação analista<->trajeto) para funcionar ;-;