package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.EmpresaModel;
import com.efficientia.efficientia.model.MotoristaModel;
import com.efficientia.efficientia.model.ParadaImprevistaModel;
import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.model.StatusTrajeto;
import com.efficientia.efficientia.model.TrajetoModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Parada Imprevista.
 *
 * Gerencia o registro e consulta de ocorrências e interrupções durante viagens pecuárias
 * na tabela 'parada_imprevista', reconstituindo todo o grafo relacional associado
 * (trajeto, motorista, caminhão, empresa e pecuarista).
 */
public class ParadaImprevistaDAO {

    private static final String SQL_BASE_SELECT = """
            SELECT
                p.id AS parada_id,
                p.data_hora_inicio AS parada_data_hora_inicio,
                p.data_hora_fim AS parada_data_hora_fim,
                p.motivo AS parada_motivo,
                p.observacao AS parada_observacao,
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
                m.id AS motorista_id,
                m.id_empresa,
                m.assinatura AS motorista_assinatura,
                m.nome AS motorista_nome,
                m.data_nascimento AS motorista_data_nascimento,
                m.senha AS motorista_senha,
                m.email AS motorista_email,
                m.telefone AS motorista_telefone,
                e.id AS empresa_id,
                e.nome AS empresa_nome,
                e.cnpj AS empresa_cnpj,
                e.codigo AS empresa_codigo,
                c.id AS caminhao_id,
                c.capacidade_maxima,
                c.placa_carreta,
                c.placa_cavalo,
                pec.id AS pecuarista_id,
                pec.cpf AS pecuarista_cpf,
                pec.assinatura AS pecuarista_assinatura,
                pec.data_nascimento AS pecuarista_data_nascimento,
                pec.nome AS pecuarista_nome,
                pec.senha AS pecuarista_senha,
                pec.email AS pecuarista_email,
                pec.telefone AS pecuarista_telefone
            FROM parada_imprevista p
            LEFT JOIN trajeto t ON t.id = p.id_trajeto
            LEFT JOIN motorista m ON m.id = t.id_motorista
            LEFT JOIN empresa e ON e.id = m.id_empresa
            LEFT JOIN caminhao c ON c.id = t.id_caminhao
            LEFT JOIN pecuarista pec ON pec.id = t.id_pecuarista
            """;

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere uma nova parada imprevista vinculada a um trajeto.
     *
     * @param parada objeto ParadaImprevistaModel contendo horários, motivo e observações
     * @return true se a inserção for realizada com êxito, false caso contrário
     */
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

    /**
     * Lista todas as paradas imprevistas registradas no sistema, com hidratação completa das entidades vinculadas.
     *
     * @return lista de paradas imprevistas ordenadas pelo ID da ocorrência
     */
    public List<ParadaImprevistaModel> listar() {
        String sql = SQL_BASE_SELECT + " ORDER BY p.id;";
        List<ParadaImprevistaModel> paradas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                paradas.add(extrairParadaImprevista(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar paradas imprevistas: " + e.getMessage());
        }

        return paradas;
    }

    /**
     * Localiza uma parada imprevista pelo seu identificador primário.
     *
     * @param id identificador único da parada imprevista
     * @return objeto ParadaImprevistaModel populado ou null caso não encontrado
     */
    public ParadaImprevistaModel buscar(int id) {
        String sql = SQL_BASE_SELECT + " WHERE p.id = ?;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairParadaImprevista(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar parada imprevista por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Atualiza as informações de uma parada imprevista previamente registrada.
     *
     * @param parada objeto ParadaImprevistaModel com os novos dados
     * @param id     identificador numérico do registro a ser atualizado
     * @return true se a modificação for bem-sucedida, false em caso de falha
     */
    public boolean atualizar(ParadaImprevistaModel parada, int id) {
        String sql = """
                UPDATE parada_imprevista SET
                    id_trajeto = ?,
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

    /**
     * Remove um registro de parada imprevista do banco de dados pelo seu ID.
     *
     * @param id identificador único do registro a ser excluído
     * @return true se o registro foi deletado, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM parada_imprevista WHERE id = ?;
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

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Retorna todas as ocorrências de parada imprevista registradas durante um trajeto específico.
     *
     * @param idTrajeto identificador do trajeto auditado
     * @return lista de paradas associadas à viagem informada
     */
    public List<ParadaImprevistaModel> listarPorTrajeto(int idTrajeto) {
        return buscarPorTrajeto(idTrajeto);
    }

    /**
     * Busca todas as paradas imprevistas vinculadas a um trajeto (método padrão de busca).
     *
     * @param idTrajeto identificador do trajeto
     * @return lista de paradas imprevistas ocorridas no trajeto
     */
    public List<ParadaImprevistaModel> buscarPorTrajeto(int idTrajeto) {
        String sql = SQL_BASE_SELECT + " WHERE p.id_trajeto = ? ORDER BY p.data_hora_inicio ASC;";
        List<ParadaImprevistaModel> paradas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idTrajeto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paradas.add(extrairParadaImprevista(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar paradas por trajeto: " + e.getMessage());
        }

        return paradas;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Extrai os dados do ResultSet hidratando completamente o objeto ParadaImprevistaModel.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto ParadaImprevistaModel completamente populado
     * @throws SQLException em caso de erro na leitura do ResultSet
     */
    private ParadaImprevistaModel extrairParadaImprevista(ResultSet rs) throws SQLException {
        TrajetoModel trajeto = null;
        int idTrajeto = rs.getInt("trajeto_id");

        if (!rs.wasNull()) {
            EmpresaModel empresa = null;
            int idEmpresa = rs.getInt("empresa_id");
            if (!rs.wasNull()) {
                empresa = new EmpresaModel(
                        idEmpresa,
                        rs.getString("empresa_nome"),
                        rs.getString("empresa_cnpj"),
                        rs.getString("empresa_codigo")
                );
            }

            MotoristaModel motorista = null;
            int idMotorista = rs.getInt("motorista_id");
            if (!rs.wasNull()) {
                Date dataNascMotorista = rs.getDate("motorista_data_nascimento");
                motorista = new MotoristaModel(
                        idMotorista,
                        empresa,
                        rs.getString("motorista_nome"),
                        rs.getString("motorista_assinatura"),
                        dataNascMotorista != null ? dataNascMotorista.toLocalDate() : null,
                        rs.getString("motorista_senha"),
                        rs.getString("motorista_email"),
                        rs.getString("motorista_telefone")
                );
            }

            CaminhaoModel caminhao = null;
            int idCaminhao = rs.getInt("caminhao_id");
            if (!rs.wasNull()) {
                caminhao = new CaminhaoModel(
                        idCaminhao,
                        empresa,
                        rs.getString("placa_cavalo"),
                        rs.getString("placa_carreta"),
                        rs.getInt("capacidade_maxima")
                );
            }

            PecuaristaModel pecuarista = null;
            int idPecuarista = rs.getInt("pecuarista_id");
            if (!rs.wasNull()) {
                Date dataNascPecuarista = rs.getDate("pecuarista_data_nascimento");
                pecuarista = new PecuaristaModel(
                        idPecuarista,
                        rs.getString("pecuarista_cpf"),
                        rs.getString("pecuarista_assinatura"),
                        dataNascPecuarista != null ? dataNascPecuarista.toLocalDate() : null,
                        rs.getString("pecuarista_nome"),
                        rs.getString("pecuarista_senha"),
                        rs.getString("pecuarista_email"),
                        rs.getString("pecuarista_telefone")
                );
            }

            String statusStr = rs.getString("status");
            StatusTrajeto status = StatusTrajeto.from(statusStr);

            Timestamp tsTrajetoInicio = rs.getTimestamp("trajeto_data_hora_inicio");
            LocalDateTime trajetoInicio = tsTrajetoInicio != null ? tsTrajetoInicio.toLocalDateTime() : null;

            Timestamp tsTrajetoFim = rs.getTimestamp("trajeto_data_hora_fim");
            LocalDateTime trajetoFim = tsTrajetoFim != null ? tsTrajetoFim.toLocalDateTime() : null;

            Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
            LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

            Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
            LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

            trajeto = new TrajetoModel(
                    idTrajeto,
                    motorista,
                    caminhao,
                    status,
                    trajetoInicio,
                    trajetoFim,
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
        }

        Timestamp tsInicio = rs.getTimestamp("parada_data_hora_inicio");
        LocalDateTime dataHoraInicio = tsInicio != null ? tsInicio.toLocalDateTime() : null;

        Timestamp tsFim = rs.getTimestamp("parada_data_hora_fim");
        LocalDateTime dataHoraFim = tsFim != null ? tsFim.toLocalDateTime() : null;

        return new ParadaImprevistaModel(
                rs.getInt("parada_id"),
                trajeto,
                dataHoraInicio,
                dataHoraFim,
                rs.getString("parada_motivo"),
                rs.getString("parada_observacao")
        );
    }

    /**
     * Vincula os atributos do modelo aos parâmetros posicionais do PreparedStatement.
     *
     * @param stmt   PreparedStatement configurado com a query SQL
     * @param parada objeto contendo os dados a serem vinculados
     * @throws SQLException se houver erro ao mapear os parâmetros JDBC
     */
    private void preencherStatement(PreparedStatement stmt, ParadaImprevistaModel parada) throws SQLException {
        if (parada.getTrajetoModel() != null && parada.getTrajetoModel().getId() > 0) {
            stmt.setInt(1, parada.getTrajetoModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        stmt.setTimestamp(2, parada.getDataHoraInicio() != null ? Timestamp.valueOf(parada.getDataHoraInicio()) : null);
        stmt.setTimestamp(3, parada.getDataHoraFim() != null ? Timestamp.valueOf(parada.getDataHoraFim()) : null);
        stmt.setString(4, parada.getMotivo());
        stmt.setString(5, parada.getObservacao());
    }
}
