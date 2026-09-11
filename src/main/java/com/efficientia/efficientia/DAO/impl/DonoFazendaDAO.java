package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.DonoFazendaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DonoFazendaDAO {

    //insert

    public void insert(DonoFazendaModel donoFazendaModel) throws SQLException {
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

            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(DonoFazendaModel donoFazendaModel) throws SQLException {
        String sql = """
    DELETE FROM dono_fazenda WHERE id = ?
""";

        try(Connection connection = ConnectionFactory.getConnection()){
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, donoFazendaModel.getId());
            stmt.executeUpdate();
        }
    }
}
