package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParadaImprevistaDAO {

    // Inserir
    public boolean inserir(ParadaImprevistaModel parada) {
        String sql = """
                INSERT INTO parada_imprevista (
                    id_trajeto,
                    data_hora_inicio,
                    data_hora_fim,
                    motivo,
                    observacao
                ) VALUES (
                    ?, ?, ?, ?, ?
                );
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, parada);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir parada imprevista: " + e.getMessage());
            return false;
        }
    }

    // Listar
    public List<ParadaImprevistaModel> listar() {
        String sql = """
                SELECT
                    -- Parada Imprevista
                    p.id AS parada_id,
                    p.data_hora_inicio AS parada_data_hora_inicio,
                    p.data_hora_fim AS parada_data_hora_fim,
                    p.motivo AS parada_motivo,
                    p.observacao AS parada_observacao,

                    -- Trajeto
                    t.id AS trajeto_id,
                    t.status,
                    t.data_hora_inicio AS trajeto_data_hora_inicio,
                    t.data_hora_fim AS trajeto_data_hora_fim,
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
                    pec.id AS pecuarista_id,
                    pec.cpf AS pecuarista_cpf,
                    pec.assinatura AS pecuarista_assinatura,
                    pec.data_nascimento AS pecuarista_data_nascimento,
                    pec.nome AS pecuarista_nome,
                    pec.senha AS pecuarista_senha,
                    pec.email AS pecuarista_email,
                    pec.telefone AS pecuarista_telefone

                FROM parada_imprevista p
                JOIN trajeto t ON t.id = p.id_trajeto
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista pec ON pec.id = t.id_pecuarista
                ORDER BY p.id;
                """;

        List<ParadaImprevistaModel> paradas = new ArrayList<>();

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

                Timestamp tsTrajetoInicio = rs.getTimestamp("trajeto_data_hora_inicio");
                LocalDateTime trajetoDataHoraInicio = tsTrajetoInicio != null ? tsTrajetoInicio.toLocalDateTime() : null;

                Timestamp tsTrajetoFim = rs.getTimestamp("trajeto_data_hora_fim");
                LocalDateTime trajetoDataHoraFim = tsTrajetoFim != null ? tsTrajetoFim.toLocalDateTime() : null;

                Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
                LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

                Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
                LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

                TrajetoModel trajeto = new TrajetoModel(
                        rs.getInt("trajeto_id"),
                        motorista,
                        caminhao,
                        status,
                        trajetoDataHoraInicio,
                        trajetoDataHoraFim,
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

                Timestamp tsParadaInicio = rs.getTimestamp("parada_data_hora_inicio");
                LocalDateTime paradaDataHoraInicio = tsParadaInicio != null ? tsParadaInicio.toLocalDateTime() : null;

                Timestamp tsParadaFim = rs.getTimestamp("parada_data_hora_fim");
                LocalDateTime paradaDataHoraFim = tsParadaFim != null ? tsParadaFim.toLocalDateTime() : null;

                ParadaImprevistaModel parada = new ParadaImprevistaModel(
                        rs.getInt("parada_id"),
                        trajeto,
                        paradaDataHoraInicio,
                        paradaDataHoraFim,
                        rs.getString("parada_motivo"),
                        rs.getString("parada_observacao")
                );

                paradas.add(parada);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar parada imprevista: " + e.getMessage());
        }

        return paradas;
    }

    // Buscar por ID
    public ParadaImprevistaModel buscar(int id) {
        String sql = """
                SELECT
                    -- Parada Imprevista
                    p.id AS parada_id,
                    p.data_hora_inicio AS parada_data_hora_inicio,
                    p.data_hora_fim AS parada_data_hora_fim,
                    p.motivo AS parada_motivo,
                    p.observacao AS parada_observacao,

                    -- Trajeto
                    t.id AS trajeto_id,
                    t.status,
                    t.data_hora_inicio AS trajeto_data_hora_inicio,
                    t.data_hora_fim AS trajeto_data_hora_fim,
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
                    pec.id AS pecuarista_id,
                    pec.cpf AS pecuarista_cpf,
                    pec.assinatura AS pecuarista_assinatura,
                    pec.data_nascimento AS pecuarista_data_nascimento,
                    pec.nome AS pecuarista_nome,
                    pec.senha AS pecuarista_senha,
                    pec.email AS pecuarista_email,
                    pec.telefone AS pecuarista_telefone

                FROM parada_imprevista p
                JOIN trajeto t ON t.id = p.id_trajeto
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista pec ON pec.id = t.id_pecuarista
                WHERE p.id = ?;
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

                    Timestamp tsTrajetoInicio = rs.getTimestamp("trajeto_data_hora_inicio");
                    LocalDateTime trajetoDataHoraInicio = tsTrajetoInicio != null ? tsTrajetoInicio.toLocalDateTime() : null;

                    Timestamp tsTrajetoFim = rs.getTimestamp("trajeto_data_hora_fim");
                    LocalDateTime trajetoDataHoraFim = tsTrajetoFim != null ? tsTrajetoFim.toLocalDateTime() : null;

                    Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
                    LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

                    Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
                    LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

                    TrajetoModel trajeto = new TrajetoModel(
                            rs.getInt("trajeto_id"),
                            motorista,
                            caminhao,
                            status,
                            trajetoDataHoraInicio,
                            trajetoDataHoraFim,
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

                    Timestamp tsParadaInicio = rs.getTimestamp("parada_data_hora_inicio");
                    LocalDateTime paradaDataHoraInicio = tsParadaInicio != null ? tsParadaInicio.toLocalDateTime() : null;

                    Timestamp tsParadaFim = rs.getTimestamp("parada_data_hora_fim");
                    LocalDateTime paradaDataHoraFim = tsParadaFim != null ? tsParadaFim.toLocalDateTime() : null;

                    return new ParadaImprevistaModel(
                            rs.getInt("parada_id"),
                            trajeto,
                            paradaDataHoraInicio,
                            paradaDataHoraFim,
                            rs.getString("parada_motivo"),
                            rs.getString("parada_observacao")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar parada imprevista: " + e.getMessage());
        }

        return null;
    }

    // Listar por Trajeto
    public List<ParadaImprevistaModel> listarPorTrajeto(int idTrajeto) {
        String sql = """
                SELECT
                    -- Parada Imprevista
                    p.id AS parada_id,
                    p.data_hora_inicio AS parada_data_hora_inicio,
                    p.data_hora_fim AS parada_data_hora_fim,
                    p.motivo AS parada_motivo,
                    p.observacao AS parada_observacao,

                    -- Trajeto
                    t.id AS trajeto_id,
                    t.status,
                    t.data_hora_inicio AS trajeto_data_hora_inicio,
                    t.data_hora_fim AS trajeto_data_hora_fim,
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
                    pec.id AS pecuarista_id,
                    pec.cpf AS pecuarista_cpf,
                    pec.assinatura AS pecuarista_assinatura,
                    pec.data_nascimento AS pecuarista_data_nascimento,
                    pec.nome AS pecuarista_nome,
                    pec.senha AS pecuarista_senha,
                    pec.email AS pecuarista_email,
                    pec.telefone AS pecuarista_telefone

                FROM parada_imprevista p
                JOIN trajeto t ON t.id = p.id_trajeto
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista pec ON pec.id = t.id_pecuarista
                WHERE p.id_trajeto = ?
                ORDER BY p.id;
                """;

        List<ParadaImprevistaModel> paradas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idTrajeto);

            try (ResultSet rs = stmt.executeQuery()) {
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

                    Timestamp tsTrajetoInicio = rs.getTimestamp("trajeto_data_hora_inicio");
                    LocalDateTime trajetoDataHoraInicio = tsTrajetoInicio != null ? tsTrajetoInicio.toLocalDateTime() : null;

                    Timestamp tsTrajetoFim = rs.getTimestamp("trajeto_data_hora_fim");
                    LocalDateTime trajetoDataHoraFim = tsTrajetoFim != null ? tsTrajetoFim.toLocalDateTime() : null;

                    Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
                    LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

                    Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
                    LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

                    TrajetoModel trajeto = new TrajetoModel(
                            rs.getInt("trajeto_id"),
                            motorista,
                            caminhao,
                            status,
                            trajetoDataHoraInicio,
                            trajetoDataHoraFim,
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

                    Timestamp tsParadaInicio = rs.getTimestamp("parada_data_hora_inicio");
                    LocalDateTime paradaDataHoraInicio = tsParadaInicio != null ? tsParadaInicio.toLocalDateTime() : null;

                    Timestamp tsParadaFim = rs.getTimestamp("parada_data_hora_fim");
                    LocalDateTime paradaDataHoraFim = tsParadaFim != null ? tsParadaFim.toLocalDateTime() : null;

                    ParadaImprevistaModel parada = new ParadaImprevistaModel(
                            rs.getInt("parada_id"),
                            trajeto,
                            paradaDataHoraInicio,
                            paradaDataHoraFim,
                            rs.getString("parada_motivo"),
                            rs.getString("parada_observacao")
                    );

                    paradas.add(parada);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar paradas imprevistas por trajeto: " + e.getMessage());
        }

        return paradas;
    }

    // Atualizar
    public boolean atualizar(ParadaImprevistaModel parada, int id) {
        String sql = """
                UPDATE parada_imprevista
                SET id_trajeto = ?,
                    data_hora_inicio = ?,
                    data_hora_fim = ?,
                    motivo = ?,
                    observacao = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, parada);
            stmt.setInt(6, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar parada imprevista: " + e.getMessage());
            return false;
        }
    }

    // Excluir
    public boolean excluir(int id) {
        String sql = """
                DELETE
                FROM parada_imprevista
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir parada imprevista: " + e.getMessage());
            return false;
        }
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(PreparedStatement stmt, ParadaImprevistaModel parada) throws SQLException {
        if (parada.getTrajeto() != null) {
            stmt.setInt(1, parada.getTrajeto().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        stmt.setObject(2, parada.getDataHoraInicio());
        stmt.setObject(3, parada.getDataHoraFim());
        stmt.setString(4, parada.getMotivo());
        stmt.setString(5, parada.getObservacao());
    }
}
