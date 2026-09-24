package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrajetoDAO {

    // Inserir
    public boolean inserir(TrajetoModel trajeto) {
        String sql = """
                INSERT INTO trajeto (
                    id_motorista,
                    id_caminhao,
                    id_pecuarista,
                    status,
                    data_hora_inicio,
                    data_hora_fim,
                    km_saida,
                    km_chegada,
                    numero_gta,
                    numero_nota_fiscal,
                    horario_embarque,
                    qtd_macho,
                    qtd_femea,
                    qtd_marruco,
                    horario_desembarque,
                    numero_curral,
                    nome_curraleiro,
                    nome_manobrista,
                    assinatura_curraleiro,
                    assinatura_manobrista,
                    assinatura_motorista
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                );
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, trajeto);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir trajeto: " + e.getMessage());
            return false;
        }
    }

    // Listar
    public List<TrajetoModel> listar() {
        String sql = """
                SELECT
                    -- Trajeto
                    t.id AS trajeto_id,
                    t.status,
                    t.data_hora_inicio,
                    t.data_hora_fim,
                    t.km_saida,
                    t.km_chegada,
                    t.numero_gta,
                    t.numero_nota_fiscal,
                    t.horario_embarque,
                    t.qtd_macho,
                    t.qtd_femea,
                    t.qtd_marruco,
                    t.horario_desembarque,
                    t.numero_curral,
                    t.nome_curraleiro,
                    t.nome_manobrista,
                    t.assinatura_curraleiro,
                    t.assinatura_manobrista,
                    t.assinatura_motorista,

                    -- Motorista
                    m.id AS motorista_id,
                    m.id_empresa,
                    m.assinatura AS motorista_assinatura,
                    m.nome AS motorista_nome,
                    m.data_nascimento AS motorista_data_nascimento,
                    m.senha AS motorista_senha,
                    m.email AS motorista_email,
                    m.telefone AS motorista_telefone,

                    -- Caminhão
                    c.id AS caminhao_id,
                    c.capacidade_maxima,
                    c.placa_carreta,
                    c.placa_cavalo,

                    -- Empresa
                    e.id AS empresa_id,
                    e.nome AS empresa_nome,
                    e.cnpj AS empresa_cnpj,

                    -- Pecuarista
                    p.id AS pecuarista_id,
                    p.cpf AS pecuarista_cpf,
                    p.assinatura AS pecuarista_assinatura,
                    p.data_nascimento AS pecuarista_data_nascimento,
                    p.nome AS pecuarista_nome,
                    p.senha AS pecuarista_senha,
                    p.email AS pecuarista_email,
                    p.telefone AS pecuarista_telefone

                FROM trajeto t
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista p ON p.id = t.id_pecuarista
                ORDER BY t.id;
                """;

        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date dataNascMotorista = rs.getDate("motorista_data_nascimento");
                LocalDate nascimentoMotorista = dataNascMotorista != null ? dataNascMotorista.toLocalDate() : null;

                MotoristaModel motorista = new MotoristaModel(
                        rs.getInt("motorista_id"),
                        new EmpresaModel(
                                rs.getInt("empresa_id"),
                                rs.getString("empresa_nome"),
                                rs.getString("empresa_cnpj")
                        ),
                        rs.getString("motorista_nome"),
                        rs.getString("motorista_assinatura"),
                        nascimentoMotorista,
                        rs.getString("motorista_senha"),
                        rs.getString("motorista_email"),
                        rs.getString("motorista_telefone")
                );

                CaminhaoModel caminhao = new CaminhaoModel(
                        rs.getInt("caminhao_id"),
                        rs.getString("placa_cavalo"),
                        rs.getString("placa_carreta"),
                        rs.getInt("capacidade_maxima")
                );

                Date dataNascPecuarista = rs.getDate("pecuarista_data_nascimento");
                LocalDate nascimentoPecuarista = dataNascPecuarista != null ? dataNascPecuarista.toLocalDate() : null;

                PecuaristaModel pecuarista = new PecuaristaModel(
                        rs.getInt("pecuarista_id"),
                        rs.getString("pecuarista_cpf"),
                        rs.getString("pecuarista_assinatura"),
                        nascimentoPecuarista,
                        rs.getString("pecuarista_nome"),
                        rs.getString("pecuarista_senha"),
                        rs.getString("pecuarista_email"),
                        rs.getString("pecuarista_telefone")
                );

                String statusStr = rs.getString("status");
                StatusTrajeto status = statusStr != null ? StatusTrajeto.valueOf(statusStr) : null;

                Timestamp tsInicio = rs.getTimestamp("data_hora_inicio");
                LocalDateTime dataHoraInicio = tsInicio != null ? tsInicio.toLocalDateTime() : null;

                Timestamp tsFim = rs.getTimestamp("data_hora_fim");
                LocalDateTime dataHoraFim = tsFim != null ? tsFim.toLocalDateTime() : null;

                Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
                LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

                Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
                LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

                TrajetoModel trajeto = new TrajetoModel(
                        rs.getInt("trajeto_id"),
                        motorista,
                        caminhao,
                        status,
                        dataHoraInicio,
                        dataHoraFim,
                        rs.getInt("km_saida"),
                        rs.getInt("km_chegada"),
                        pecuarista,
                        rs.getString("numero_gta"),
                        rs.getString("numero_nota_fiscal"),
                        horarioEmbarque,
                        rs.getInt("qtd_macho"),
                        rs.getInt("qtd_femea"),
                        rs.getInt("qtd_marruco"),
                        horarioDesembarque,
                        rs.getString("numero_curral"),
                        rs.getString("nome_curraleiro"),
                        rs.getString("nome_manobrista")
                );

                trajeto.setAssinaturaCurraleiro(rs.getString("assinatura_curraleiro"));
                trajeto.setAssinaturaManobrista(rs.getString("assinatura_manobrista"));
                trajeto.setAssinaturaMotorista(rs.getString("assinatura_motorista"));

                trajetos.add(trajeto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar trajeto: " + e.getMessage());
        }

        return trajetos;
    }

    // Buscar por ID
    public TrajetoModel buscar(int id) {
        String sql = """
                SELECT
                    -- Trajeto
                    t.id AS trajeto_id,
                    t.status,
                    t.data_hora_inicio,
                    t.data_hora_fim,
                    t.km_saida,
                    t.km_chegada,
                    t.numero_gta,
                    t.numero_nota_fiscal,
                    t.horario_embarque,
                    t.qtd_macho,
                    t.qtd_femea,
                    t.qtd_marruco,
                    t.horario_desembarque,
                    t.numero_curral,
                    t.nome_curraleiro,
                    t.nome_manobrista,
                    t.assinatura_curraleiro,
                    t.assinatura_manobrista,
                    t.assinatura_motorista,

                    -- Motorista
                    m.id AS motorista_id,
                    m.id_empresa,
                    m.assinatura AS motorista_assinatura,
                    m.nome AS motorista_nome,
                    m.data_nascimento AS motorista_data_nascimento,
                    m.senha AS motorista_senha,
                    m.email AS motorista_email,
                    m.telefone AS motorista_telefone,

                    -- Caminhão
                    c.id AS caminhao_id,
                    c.capacidade_maxima,
                    c.placa_carreta,
                    c.placa_cavalo,

                    -- Empresa
                    e.id AS empresa_id,
                    e.nome AS empresa_nome,
                    e.cnpj AS empresa_cnpj,

                    -- Pecuarista
                    p.id AS pecuarista_id,
                    p.cpf AS pecuarista_cpf,
                    p.assinatura AS pecuarista_assinatura,
                    p.data_nascimento AS pecuarista_data_nascimento,
                    p.nome AS pecuarista_nome,
                    p.senha AS pecuarista_senha,
                    p.email AS pecuarista_email,
                    p.telefone AS pecuarista_telefone

                FROM trajeto t
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista p ON p.id = t.id_pecuarista
                WHERE t.id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dataNascMotorista = rs.getDate("motorista_data_nascimento");
                    LocalDate nascimentoMotorista = dataNascMotorista != null ? dataNascMotorista.toLocalDate() : null;

                    MotoristaModel motorista = new MotoristaModel(
                            rs.getInt("motorista_id"),
                            new EmpresaModel(
                                    rs.getInt("empresa_id"),
                                    rs.getString("empresa_nome"),
                                    rs.getString("empresa_cnpj")
                            ),
                            rs.getString("motorista_nome"),
                            rs.getString("motorista_assinatura"),
                            nascimentoMotorista,
                            rs.getString("motorista_senha"),
                            rs.getString("motorista_email"),
                            rs.getString("motorista_telefone")
                    );

                    CaminhaoModel caminhao = new CaminhaoModel(
                            rs.getInt("caminhao_id"),
                            rs.getString("placa_cavalo"),
                            rs.getString("placa_carreta"),
                            rs.getInt("capacidade_maxima")
                    );

                    Date dataNascPecuarista = rs.getDate("pecuarista_data_nascimento");
                    LocalDate nascimentoPecuarista = dataNascPecuarista != null ? dataNascPecuarista.toLocalDate() : null;

                    PecuaristaModel pecuarista = new PecuaristaModel(
                            rs.getInt("pecuarista_id"),
                            rs.getString("pecuarista_cpf"),
                            rs.getString("pecuarista_assinatura"),
                            nascimentoPecuarista,
                            rs.getString("pecuarista_nome"),
                            rs.getString("pecuarista_senha"),
                            rs.getString("pecuarista_email"),
                            rs.getString("pecuarista_telefone")
                    );

                    String statusStr = rs.getString("status");
                    StatusTrajeto status = statusStr != null ? StatusTrajeto.valueOf(statusStr) : null;

                    Timestamp tsInicio = rs.getTimestamp("data_hora_inicio");
                    LocalDateTime dataHoraInicio = tsInicio != null ? tsInicio.toLocalDateTime() : null;

                    Timestamp tsFim = rs.getTimestamp("data_hora_fim");
                    LocalDateTime dataHoraFim = tsFim != null ? tsFim.toLocalDateTime() : null;

                    Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
                    LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

                    Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
                    LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

                    TrajetoModel trajeto = new TrajetoModel(
                            rs.getInt("trajeto_id"),
                            motorista,
                            caminhao,
                            status,
                            dataHoraInicio,
                            dataHoraFim,
                            rs.getInt("km_saida"),
                            rs.getInt("km_chegada"),
                            pecuarista,
                            rs.getString("numero_gta"),
                            rs.getString("numero_nota_fiscal"),
                            horarioEmbarque,
                            rs.getInt("qtd_macho"),
                            rs.getInt("qtd_femea"),
                            rs.getInt("qtd_marruco"),
                            horarioDesembarque,
                            rs.getString("numero_curral"),
                            rs.getString("nome_curraleiro"),
                            rs.getString("nome_manobrista")
                    );

                    trajeto.setAssinaturaCurraleiro(rs.getString("assinatura_curraleiro"));
                    trajeto.setAssinaturaManobrista(rs.getString("assinatura_manobrista"));
                    trajeto.setAssinaturaMotorista(rs.getString("assinatura_motorista"));

                    return trajeto;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajeto: " + e.getMessage());
        }

        return null;
    }

    // Atualizar
    public boolean atualizar(TrajetoModel trajeto, int id) {
        String sql = """
                UPDATE trajeto
                SET id_motorista = ?,
                    id_caminhao = ?,
                    id_pecuarista = ?,
                    status = ?,
                    data_hora_inicio = ?,
                    data_hora_fim = ?,
                    km_saida = ?,
                    km_chegada = ?,
                    numero_gta = ?,
                    numero_nota_fiscal = ?,
                    horario_embarque = ?,
                    qtd_macho = ?,
                    qtd_femea = ?,
                    qtd_marruco = ?,
                    horario_desembarque = ?,
                    numero_curral = ?,
                    nome_curraleiro = ?,
                    nome_manobrista = ?,
                    assinatura_curraleiro = ?,
                    assinatura_manobrista = ?,
                    assinatura_motorista = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, trajeto);
            stmt.setInt(22, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar trajeto: " + e.getMessage());
            return false;
        }
    }

    // Excluir
    public boolean excluir(int id) {
        String sql = """
                DELETE
                FROM trajeto
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir trajeto: " + e.getMessage());
            return false;
        }
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(PreparedStatement stmt, TrajetoModel trajeto) throws SQLException {
        if (trajeto.getMotoristaModel() != null) {
            stmt.setInt(1, trajeto.getMotoristaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        if (trajeto.getCaminhaoModel() != null) {
            stmt.setInt(2, trajeto.getCaminhaoModel().getId());
        } else {
            stmt.setNull(2, Types.INTEGER);
        }

        if (trajeto.getPecuarista() != null) {
            stmt.setInt(3, trajeto.getPecuarista().getId());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }

        stmt.setString(4, trajeto.getStatus() != null ? trajeto.getStatus().name() : null);
        stmt.setObject(5, trajeto.getDataHoraInicio());
        stmt.setObject(6, trajeto.getDataHoraFim());
        stmt.setInt(7, trajeto.getKmSaida());
        stmt.setInt(8, trajeto.getKmChegada());
        stmt.setString(9, trajeto.getNumeroGTA());
        stmt.setString(10, trajeto.getNumeroNotaFiscal());
        stmt.setObject(11, trajeto.getHorarioEmbarque());
        stmt.setInt(12, trajeto.getQtdMacho());
        stmt.setInt(13, trajeto.getQtdFemea());
        stmt.setInt(14, trajeto.getQtdMarruco());
        stmt.setObject(15, trajeto.getHorarioDesembarque());
        stmt.setString(16, trajeto.getNumeroCurral());
        stmt.setString(17, trajeto.getNomeCurraleiro());
        stmt.setString(18, trajeto.getNomeManobrista());
        stmt.setString(19, trajeto.getAssinaturaCurraleiro());
        stmt.setString(20, trajeto.getAssinaturaManobrista());
        stmt.setString(21, trajeto.getAssinaturaMotorista());
    }
}
