package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.PecuaristaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class PecuaristaDAO {

    //insert

    public boolean inserir(PecuaristaModel pecuaristaModel){
        String sql = """
    INSERT INTO pecuarista (cpf,
                          assinatura,
                          data_nascimento,
                          nome,
                          senha,
                          email,
                          telefone)
    VALUES (?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?)
""";

        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            preencherStatement(stmt, pecuaristaModel);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch (SQLException e){
            System.out.println("Erro ao inserir pecuarista: " + e.getMessage());
            return false;
        }
    }

    //select

    public List<PecuaristaModel> listar(){
        String sql = """
    SELECT * FROM pecuarista
    ORDER BY id;
                """;

        List<PecuaristaModel> listaPecuarista = new ArrayList<>();

        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){

                Date dataNascimento = rs.getDate("data_nascimento");

                PecuaristaModel pecuaristaModel = new PecuaristaModel(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("assinatura"),
                        dataNascimento != null
                            ? dataNascimento.toLocalDate()
                        : null,
                        rs.getString("nome"),
                        rs.getString("senha"),
                        rs.getString("email"),
                        rs.getString("telefone")
                );
                listaPecuarista.add(pecuaristaModel);

            }
        }catch (SQLException e){
            System.out.println("Erro ao listar Pecuarista: " + e.getMessage());
        }

        return listaPecuarista;

    }

    //update

    public boolean atualizar(PecuaristaModel pecuaristaModel, int id){
        String sql = """
        UPDATE pecuarista
        SET cpf = ?,
        assinatura = ?,
        data_nascimento = ?,
        nome = ?,
        senha = ?,
        email = ?,
        telefone = ?
        WHERE id = ?;
        """;

        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);){
            preencherStatement(stmt, pecuaristaModel);
            stmt.setInt(8, id);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch (SQLException e){
            System.out.println("Erro ao atualizar pecuarista: " + e.getMessage());
            return false;
        }
    }

    //delete

    public boolean excluir(int id){
        String sql = """
    DELETE FROM pecuarista WHERE id = ?
""";

        try(Connection connection = ConnectionFactory.getConnection()){
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch (SQLException e){
            System.out.println("Erro ao excluir pecuarista: " + e.getMessage());
            return false;
        }
    }

    //Busca por id

    public PecuaristaModel buscar(int id){
        String sql = """
        SELECT * FROM pecuarista WHERE id = ?;
        """;
        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Date dataNascimento = rs.getDate("data_nascimento");
                LocalDate nascimento = dataNascimento != null ? dataNascimento.toLocalDate() : null;

                PecuaristaModel pecuaristaModel = new PecuaristaModel(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("assinatura"),
                        nascimento,
                        rs.getString("nome"),
                        rs.getString("senha"),
                        rs.getString("email"),
                        rs.getString("telefone"));

                return pecuaristaModel;
            }
            else{
                return null;
            }


        }catch (SQLException e){
            System.out.println("Erro ao buscar pecuarista: " + e.getMessage());
            return null;
        }
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(PreparedStatement stmt, PecuaristaModel pecuaristaModel) throws SQLException {
        stmt.setString(1, pecuaristaModel.getCpf());
        stmt.setString(2, pecuaristaModel.getAssinatura());

        if (pecuaristaModel.getDataNascimento() != null) {
            stmt.setDate(3, Date.valueOf(pecuaristaModel.getDataNascimento()));
        } else {
            stmt.setDate(3, null);
        }

        stmt.setString(4, pecuaristaModel.getNome());
        stmt.setString(5, pecuaristaModel.getSenha());
        stmt.setString(6, pecuaristaModel.getEmail());
        stmt.setString(7, pecuaristaModel.getTelefone());
    }
}
