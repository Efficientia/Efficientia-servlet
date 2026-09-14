package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.FazendaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FazendaDAO {

    //insert

    public boolean inserir(FazendaModel fazendaModel) throws SQLException {

        String sql = """
               INSERT INTO Fazenda 
               VALUES (?,?,?);
               """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setInt(1, fazendaModel.getDonoFazendaModel().getId());
            stmt.setInt(2, fazendaModel.getEnderecoModel().getId());
            stmt.setString(3, fazendaModel.getNome());

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao inserir Fazenda: " + e.getMessage());
            return false;
        }
    }

    //select

    public List<FazendaModel> listar() throws SQLException {
        String sql = """
                   SELECT * FROM Fazenda
        """;

        List<FazendaModel> fazendaModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                FazendaModel fazendaModel = new FazendaModel(
                        rs.getInt("id"),
                        rs.getInt("id_dono_fazenda"),
                        rs.getInt("id_endereco"),
                        rs.getString("nome")
                );

                fazendaModels.add(fazendaModel);
            }

        }

        return fazendaModels;
    }

    //update

    public boolean atualizar(FazendaModel fazendaModel, int id) throws SQLException {
        String sql = """
                   UPDATE fazenda
                   SET 
                   id_dono_fazenda = ?,
                   id_endereco = ?,
                   nome = ?
                   WHERE id = ?;
        """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, fazendaModel.getDonoFazendaModel().getId());
            stmt.setInt(2, fazendaModel.getEnderecoModel().getId());
            stmt.setString(3, fazendaModel.getNome());
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao atualizar Fazenda: " + e.getMessage());
            return false;
        }
    }

    //delete

    public boolean excluir(int id) throws SQLException {
        String sql = """
                   DELETE FROM Fazenda
                   WHERE id = ?;
        """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao excluir Fazenda: " + e.getMessage());
            return false;
        }
    }

}
