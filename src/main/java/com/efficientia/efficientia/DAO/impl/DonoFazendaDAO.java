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
INSERT INTO dono_fazenda (id,
                          cpf,
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
        ?,
        ?)""";

        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setInt(1, donoFazendaModel.getId());
            stmt.setString(2, donoFazendaModel.getCpf());
            stmt.setString(3, donoFazendaModel.getAssinatura());
            stmt.setDate(4, java.sql.Date.valueOf(donoFazendaModel.getDataNascimento()));
            stmt.setString(5, donoFazendaModel.getNome());
            stmt.setString(6, donoFazendaModel.getSenha());
            stmt.setString(7, donoFazendaModel.getEmail());
            stmt.setString(8, donoFazendaModel.getTelefone());

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
