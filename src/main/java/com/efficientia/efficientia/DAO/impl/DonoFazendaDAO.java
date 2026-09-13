package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.DonoFazendaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.sql.Types;

public class DonoFazendaDAO {

    //insert

    public boolean inserir(DonoFazendaModel donoFazendaModel) throws SQLException {
        String sql = """
    INSERT INTO dono_fazenda (cpf,
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

            stmt.setString(1, donoFazendaModel.getCpf());
            stmt.setString(2, donoFazendaModel.getAssinatura());
            stmt.setDate(3, java.sql.Date.valueOf(donoFazendaModel.getDataNascimento()));
            stmt.setString(4, donoFazendaModel.getNome());
            stmt.setString(5, donoFazendaModel.getSenha());
            stmt.setString(6, donoFazendaModel.getEmail());
            stmt.setString(7, donoFazendaModel.getTelefone());

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch (SQLException e){
            System.out.println("Erro ao inserir donoFazenda: " + e.getMessage());
            return false;
        }
    }

    //select

    public List<DonoFazendaModel> listar() throws SQLException {
        String sql = """
    SELECT * FROM dono_fazenda
    ORDER BY id;
                """;

        List<DonoFazendaModel> listaDonoFazenda = new ArrayList<>();

        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){

                Date dataNascimento = rs.getDate("data_nascimento");

                DonoFazendaModel donoFazendaModel = new DonoFazendaModel(
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
                listaDonoFazenda.add(donoFazendaModel);

            }
        }catch (SQLException e){
            System.out.println("Erro ao listar DonoFazenda: " + e.getMessage());
        }

        return listaDonoFazenda;

    }

    //update

    public boolean atualizar(DonoFazendaModel donoFazendaModel, int id) throws SQLException {
        String sql = """
        UPDATE dono_fazenda
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
            stmt.setString(1, donoFazendaModel.getCpf());
            stmt.setString(2, donoFazendaModel.getAssinatura());

            if (donoFazendaModel.getDataNascimento() != null){
                stmt.setDate(3,
                        Date.valueOf(donoFazendaModel.getDataNascimento()));
            }
            else{
                stmt.setDate(3, null);
            }

            stmt.setString(4, donoFazendaModel.getNome());
            stmt.setString(5, donoFazendaModel.getSenha());
            stmt.setString(6, donoFazendaModel.getEmail());
            stmt.setString(7, donoFazendaModel.getTelefone());
            stmt.setInt(8, id);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch (SQLException e){
            System.out.println("Erro ao atualizar donoFazenda: " + e.getMessage());
            return false;
        }
    }

    //delete

    public boolean excluir(DonoFazendaModel donoFazendaModel) throws SQLException {
        String sql = """
    DELETE FROM dono_fazenda WHERE id = ?
""";

        try(Connection connection = ConnectionFactory.getConnection()){
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, donoFazendaModel.getId());
            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch (SQLException e){
            System.out.println("Erro ao excluir donoFazenda: " + e.getMessage());
            return false;
        }
    }
}
