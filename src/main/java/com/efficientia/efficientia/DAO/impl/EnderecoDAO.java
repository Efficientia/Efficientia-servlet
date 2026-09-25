package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EnderecoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {

    //insert

    public boolean inserir(EnderecoModel enderecoModel){
        String sql = """
                INSERT INTO endereco (cep,
                                      tipo,
                                      numero,
                                      rua,
                                      cidade,
                                      estado,
                                      pais,
                                      complemento)
                VALUES (?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?);
""";

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){

            preencherStatement(stmt, enderecoModel);

            int linhasAfetadas = stmt.executeUpdate();

            return  linhasAfetadas > 0;
        }catch (SQLException e) {
            System.out.println("Erro ao inserir Endereco: " + e.getMessage());
            return false;
        }
    }

    //select
    public List<EnderecoModel> listar() throws SQLException {
        String sql = """
                    SELECT * FROM endereco ORDER BY id;
                    """;
        List<EnderecoModel> enderecoModels = new ArrayList<>();

        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);){
            ResultSet rs = stmt.executeQuery();



            while(rs.next()){
                EnderecoModel enderecoModel = new EnderecoModel(
                        rs.getInt("id"),
                        rs.getString("cep"),
                        rs.getString("tipo"),
                        rs.getString("numero"),
                        rs.getString("rua"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("pais"),
                        rs.getString("complemento"));
                        enderecoModels.add(enderecoModel);
            }
        }catch (SQLException e) {
            System.out.println("Erro ao listar Endereco: " + e.getMessage());
        }
        return enderecoModels;
    }

    //update

    public boolean atualizar(EnderecoModel enderecoModel, int id){
        String sql = """
                    UPDATE endereco SET
                     cep = ?,
                     tipo = ?,
                     numero = ?,
                     rua = ?,
                     cidade = ?,
                     estado = ?,
                     pais = ?,
                     complemento = ?
                     WHERE id = ?;
                             """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){

           preencherStatement(stmt, enderecoModel);
           stmt.setInt(9, id);

           int linhasAfetadas = stmt.executeUpdate();

           return  linhasAfetadas > 0;
        }catch (SQLException e) {
            System.out.println("Erro ao atualizar Endereco: " + e.getMessage());
            return false;
        }
    }

    //delete

    public boolean excluir(int id){
        String sql = """
                    DELETE FROM endereco WHERE id = ?;
        """;

        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            return  linhasAfetadas > 0;
        }catch (SQLException e) {
            System.out.println("Erro ao excluir Endereco: " + e.getMessage());
            return false;
        }
    }

    //Busca por id

    public EnderecoModel buscar(int id){
        String sql = """
                    SELECT * FROM endereco WHERE id = ?;
        """;


        try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();


            if (rs.next()) {
                return new EnderecoModel(
                        rs.getInt("id"),
                        rs.getString("cep"),
                        rs.getString("tipo"),
                        rs.getString("numero"),
                        rs.getString("rua"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("pais"),
                        rs.getString("complemento")
                );
            } else {
                return null;
            }
        }catch (SQLException e) {
            System.out.println("Erro ao buscar Endereco: " + e.getMessage());
            return null;
        }
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(PreparedStatement stmt, EnderecoModel endereco) throws SQLException {
        stmt.setString(1, endereco.getCep());
        stmt.setString(2, endereco.getTipo());
        stmt.setString(3, endereco.getNumero());
        stmt.setString(4, endereco.getRua());
        stmt.setString(5, endereco.getCidade());
        stmt.setString(6, endereco.getEstado());
        stmt.setString(7, endereco.getPais());
        stmt.setString(8, endereco.getComplemento());
    }
}
