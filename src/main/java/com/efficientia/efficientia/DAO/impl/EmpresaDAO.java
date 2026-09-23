package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.EmpresaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.sql.Types;


public class EmpresaDAO {
    public boolean inserir(EmpresaModel empresa) {
        String sql = """
                INSERT INTO empresa (nome, cnpj)
                VALUES (?, ?);""";

        try(Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.out.println("Erro ao inserir empresaModel: " + e.getMessage());
            return false;
        }
    }

    public List<EmpresaModel> listar() {
        String sql = """
                SELECT * FROM empresa
                ORDER BY id;""";

        List<EmpresaModel> empresas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);){
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                EmpresaModel empresaModel = new EmpresaModel(rs.getInt(
                        "id"),
                        rs.getString("nome"),
                        rs.getString("cnpj"));

                empresas.add(empresaModel);
            }
        }catch(SQLException e){
            System.out.println("Erro ao listar empresaModel: " + e.getMessage());
        }
        return empresas;
    }

    public boolean atualizar(EmpresaModel empresa, int id) {
        String sql = """
                UPDATE empresa
                SET nome = ?, 
                cnpj = ? 
                WHERE id = ?;
        """;

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getNome());
            stmt.setInt(3, id);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar empresaModel: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = """
                DELETE FROM empresa WHERE id = ?;""";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        }catch(SQLException e){
            System.out.println("Erro ao excluir empresaModel: " + e.getMessage());
            return false;
        }
    }
}
