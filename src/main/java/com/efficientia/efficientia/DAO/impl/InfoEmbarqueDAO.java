package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InfoEmbarqueDAO {

    // Inserir
    public boolean inserir(InfoEmbarqueModel infoEmbarque) {
        String sql = """
                INSERT INTO info_embarque (
                    nome,
                    id_trajeto
                ) VALUES (
                    ?, ?
                );
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, infoEmbarque);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir InfoEmbarque: " + e.getMessage());
            return false;
        }
    }

    // Listar
    public List<InfoEmbarqueModel> listar() {
        String sql = """
                SELECT
                    -- InfoEmbarque
                    i.id AS info_embarque_id,
                    i.nome AS info_embarque_nome,

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

                FROM info_embarque i
                JOIN trajeto t ON t.id = i.id_trajeto
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista p ON p.id = t.id_pecuarista
                ORDER BY i.id;
                """;

        List<InfoEmbarqueModel> infoEmbarques = new ArrayList<>();

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

                InfoEmbarqueModel infoEmbarque = new InfoEmbarqueModel(
                        rs.getInt("info_embarque_id"),
                        rs.getString("info_embarque_nome"),
                        trajeto
                );

                infoEmbarques.add(infoEmbarque);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar InfoEmbarque: " + e.getMessage());
        }

        return infoEmbarques;
    }

    // Buscar por ID
    public InfoEmbarqueModel buscar(int id) {
        String sql = """
                SELECT
                    -- InfoEmbarque
                    i.id AS info_embarque_id,
                    i.nome AS info_embarque_nome,

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

                FROM info_embarque i
                JOIN trajeto t ON t.id = i.id_trajeto
                JOIN motorista m ON m.id = t.id_motorista
                JOIN caminhao c ON c.id = t.id_caminhao
                JOIN empresa e ON e.id = m.id_empresa
                JOIN pecuarista p ON p.id = t.id_pecuarista
                WHERE i.id = ?;
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

                    return new InfoEmbarqueModel(
                            rs.getInt("info_embarque_id"),
                            rs.getString("info_embarque_nome"),
                            trajeto
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar InfoEmbarque: " + e.getMessage());
        }

        return null;
    }

    // Atualizar
    public boolean atualizar(InfoEmbarqueModel infoEmbarque, int id) {
        String sql = """
                UPDATE info_embarque
                SET nome = ?,
                    id_trajeto = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, infoEmbarque);
            stmt.setInt(3, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar InfoEmbarque: " + e.getMessage());
            return false;
        }
    }

    // Excluir
    public boolean excluir(int id) {
        String sql = """
                DELETE
                FROM info_embarque
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir InfoEmbarque: " + e.getMessage());
            return false;
        }
    }

    // Mapeamento do PreparedStatement
    private void preencherStatement(PreparedStatement stmt, InfoEmbarqueModel infoEmbarque) throws SQLException {
        stmt.setString(1, infoEmbarque.getNome());

        if (infoEmbarque.getTrajeto() != null) {
            stmt.setInt(2, infoEmbarque.getTrajeto().getId());
        } else {
            stmt.setNull(2, Types.INTEGER);
        }
    }
}
