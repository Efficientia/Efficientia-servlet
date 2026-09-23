package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.model.PropriedadeModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropriedadeDAO {

    //insert

    public boolean inserir(PropriedadeModel propriedadeModel){

        String sql = """
               INSERT INTO propriedade (id_pecuarista, id_endereco, nome)
               VALUES (?, ?, ?);
               """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setInt(1, propriedadeModel.getPecuaristaModel().getId());
            stmt.setInt(2, propriedadeModel.getEnderecoModel().getId());
            stmt.setString(3, propriedadeModel.getNome());

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao inserir Propriedade: " + e.getMessage());
            return false;
        }
    }

    //select

    public List<PropriedadeModel> listar(){
        String sql = """
                   SELECT * FROM propriedade ORDER BY id;
        """;

        List<PropriedadeModel> propriedadeModels = new ArrayList<>();
        PecuaristaDAO pecuaristaDAO = new PecuaristaDAO();
        EnderecoDAO enderecoDAO = new EnderecoDAO();

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                PropriedadeModel propriedadeModel = new PropriedadeModel(
                        rs.getInt("id"),
                        pecuaristaDAO.buscar(rs.getInt("id_pecuarista")),
                        enderecoDAO.buscar(rs.getInt("id_endereco")),
                        rs.getString("nome")
                );

                propriedadeModels.add(propriedadeModel);
            }

        }catch (SQLException e){
            System.out.println("Erro ao listar Propriedade: " + e.getMessage());
        }

        return propriedadeModels;
    }

    //update

    public boolean atualizar(PropriedadeModel propriedadeModel, int id){
        String sql = """
                   UPDATE propriedade
                   SET 
                   id_pecuarista = ?,
                   id_endereco = ?,
                   nome = ?
                   WHERE id = ?;
        """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, propriedadeModel.getPecuaristaModel().getId());
            stmt.setInt(2, propriedadeModel.getEnderecoModel().getId());
            stmt.setString(3, propriedadeModel.getNome());
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao atualizar Propriedade: " + e.getMessage());
            return false;
        }
    }

    //delete

    public boolean excluir(int id){
        String sql = """
                   DELETE FROM propriedade
                   WHERE id = ?;
        """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao excluir Propriedade: " + e.getMessage());
            return false;
        }
    }

    //Busca por id

    public PropriedadeModel buscar(int id){
        String sql = """
                    SELECT * FROM propriedade WHERE id = ?;
        """;

        PecuaristaDAO pecuaristaDAO = new PecuaristaDAO();
        EnderecoDAO enderecoDAO = new EnderecoDAO();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PropriedadeModel(
                        rs.getInt("id"),
                        pecuaristaDAO.buscar(rs.getInt("id_pecuarista")),
                        enderecoDAO.buscar(rs.getInt("id_endereco")),
                        rs.getString("nome")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Propriedade: " + e.getMessage());
            return null;
        }
    }

}
