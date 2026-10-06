package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.EmpresaModel;
import com.efficientia.efficientia.model.InfoEmbarqueModel;
import com.efficientia.efficientia.model.MotoristaModel;
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
 * Data Access Object (DAO) para a entidade Informações de Embarque.
 *
 * Gerencia a persistência de registros documentais e dados adicionais de embarque
 * na tabela 'info_embarque', realizando junções com as tabelas de trajeto,
 * motorista, caminhão, empresa e pecuarista para hidratação completa dos objetos.
 */
public class InfoEmbarqueDAO {

    private static final String SQL_BASE_SELECT = """
            SELECT
                i.id AS info_embarque_id,
                i.nome AS info_embarque_nome,
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
                p.id AS pecuarista_id,
                p.cpf AS pecuarista_cpf,
                p.assinatura AS pecuarista_assinatura,
                p.data_nascimento AS pecuarista_data_nascimento,
                p.nome AS pecuarista_nome,
                p.senha AS pecuarista_senha,
                p.email AS pecuarista_email,
                p.telefone AS pecuarista_telefone
            FROM info_embarque i
            LEFT JOIN trajeto t ON t.id = i.id_trajeto
            LEFT JOIN motorista m ON m.id = t.id_motorista
            LEFT JOIN empresa e ON e.id = m.id_empresa
            LEFT JOIN caminhao c ON c.id = t.id_caminhao
            LEFT JOIN pecuarista p ON p.id = t.id_pecuarista
            """;

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo registro de informação de embarque associado a um trajeto.
     *
     * @param infoEmbarque objeto InfoEmbarqueModel a ser cadastrado
     * @return true se a inserção for realizada com êxito, false em caso de falha
     */
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

    /**
     * Lista todos os registros de informações de embarque, reconstituindo o trajeto completo e seus agentes via JOIN.
     *
     * @return lista de objetos InfoEmbarqueModel ordenados por ID
     */
    public List<InfoEmbarqueModel> listar() {
        String sql = SQL_BASE_SELECT + " ORDER BY i.id;";
        List<InfoEmbarqueModel> infoEmbarques = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                infoEmbarques.add(extrairInfoEmbarque(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar InfoEmbarque: " + e.getMessage());
        }

        return infoEmbarques;
    }

    /**
     * Localiza uma informação de embarque por ID, montando a árvore de objetos do trajeto relacionado.
     *
     * @param id identificador único da informação de embarque
     * @return objeto InfoEmbarqueModel populado ou null caso não encontrado
     */
    public InfoEmbarqueModel buscar(int id) {
        String sql = SQL_BASE_SELECT + " WHERE i.id = ?;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairInfoEmbarque(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar InfoEmbarque por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Atualiza o nome e o trajeto de um registro de informação de embarque existente.
     *
     * @param infoEmbarque objeto com os novos valores
     * @param id           identificador numérico do registro a ser atualizado
     * @return true se a atualização ocorrer com êxito, false caso contrário
     */
    public boolean atualizar(InfoEmbarqueModel infoEmbarque, int id) {
        String sql = """
                UPDATE info_embarque SET
                    nome = ?,
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

    /**
     * Remove um registro de informação de embarque a partir de seu ID.
     *
     * @param id identificador único do registro a ser deletado
     * @return true se o registro foi excluído, false em caso de falha
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM info_embarque WHERE id = ?;
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

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Localiza a informação de embarque vinculada a um trajeto específico.
     *
     * @param idTrajeto identificador do trajeto
     * @return objeto InfoEmbarqueModel associado ao trajeto ou null caso não exista
     */
    public InfoEmbarqueModel buscarPorTrajeto(int idTrajeto) {
        String sql = SQL_BASE_SELECT + " WHERE i.id_trajeto = ?;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idTrajeto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairInfoEmbarque(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar InfoEmbarque por trajeto: " + e.getMessage());
        }

        return null;
    }

    /**
     * Busca informações de embarque por nome, suportando correspondência exata, parcial e case-insensitive.
     *
     * @param termo termo ou palavras-chave de busca
     * @return lista de informações de embarque encontradas
     */
    public List<InfoEmbarqueModel> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listar();
        }

        String[] tokens = termo.trim().split("\\s+");
        StringBuilder sql = new StringBuilder(SQL_BASE_SELECT + " WHERE 1=1");

        for (int i = 0; i < tokens.length; i++) {
            sql.append(" AND LOWER(i.nome) LIKE ?");
        }
        sql.append(" ORDER BY i.nome ASC, i.id ASC;");

        List<InfoEmbarqueModel> lista = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, "%" + tokens[i].toLowerCase() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairInfoEmbarque(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar InfoEmbarque por nome: " + e.getMessage());
        }

        return lista;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Extrai os dados do ResultSet hidratando completamente o objeto InfoEmbarqueModel e sua árvore de Trajeto.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto InfoEmbarqueModel completamente construído
     * @throws SQLException em caso de erro na leitura do ResultSet
     */
    private InfoEmbarqueModel extrairInfoEmbarque(ResultSet rs) throws SQLException {
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
            StatusTrajeto status = statusStr != null ? StatusTrajeto.valueOf(statusStr) : null;

            Timestamp tsInicio = rs.getTimestamp("data_hora_inicio");
            LocalDateTime dataHoraInicio = tsInicio != null ? tsInicio.toLocalDateTime() : null;

            Timestamp tsFim = rs.getTimestamp("data_hora_fim");
            LocalDateTime dataHoraFim = tsFim != null ? tsFim.toLocalDateTime() : null;

            Timestamp tsEmbarque = rs.getTimestamp("horario_embarque");
            LocalDateTime horarioEmbarque = tsEmbarque != null ? tsEmbarque.toLocalDateTime() : null;

            Timestamp tsDesembarque = rs.getTimestamp("horario_desembarque");
            LocalDateTime horarioDesembarque = tsDesembarque != null ? tsDesembarque.toLocalDateTime() : null;

            trajeto = new TrajetoModel(
                    idTrajeto,
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
        }

        return new InfoEmbarqueModel(
                rs.getInt("info_embarque_id"),
                rs.getString("info_embarque_nome"),
                trajeto
        );
    }

    /**
     * Vincula os atributos do modelo aos parâmetros do PreparedStatement.
     *
     * @param stmt         PreparedStatement a ser configurado
     * @param infoEmbarque objeto com os dados a serem vinculados
     * @throws SQLException se houver erro ao atribuir os parâmetros
     */
    private void preencherStatement(PreparedStatement stmt, InfoEmbarqueModel infoEmbarque) throws SQLException {
        stmt.setString(1, infoEmbarque.getNome());

        if (infoEmbarque.getTrajetoModel() != null && infoEmbarque.getTrajetoModel().getId() > 0) {
            stmt.setInt(2, infoEmbarque.getTrajetoModel().getId());
        } else {
            stmt.setNull(2, Types.INTEGER);
        }
    }
}
