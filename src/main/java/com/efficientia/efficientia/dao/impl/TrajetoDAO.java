package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.EmpresaModel;
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
 * Data Access Object (DAO) para a entidade Trajeto / Viagem.
 *
 * É o DAO central da operação logística de transporte pecuário.
 * Responsável pela persistência e consulta dos dados da tabela 'trajeto',
 * realizando junções (JOIN) com 'motorista', 'caminhao', 'empresa' e 'pecuarista'
 * para carregar e reconstituir a integridade relacional do trajeto.
 */
public class TrajetoDAO {

    private static final String BASE_SELECT = """
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
                e.codigo AS empresa_codigo,

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
            """;

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo trajeto no banco de dados.
     *
     * @param trajeto objeto TrajetoModel contendo os dados operacionais, fiscais e sanitários da viagem
     * @return true se o trajeto foi registrado com sucesso, false caso ocorra falha
     */
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
                    ?, ?, ?, ?::status_trajeto, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
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

    /**
     * Lista todos os trajetos cadastrados no sistema, com hidratação completa dos objetos vinculados.
     *
     * @return lista de objetos TrajetoModel ordenados pelo identificador do trajeto
     */
    public List<TrajetoModel> listar() {
        String sql = BASE_SELECT + " ORDER BY t.id;";
        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trajetos.add(extrairTrajeto(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar trajeto: " + e.getMessage());
        }

        return trajetos;
    }

    /**
     * Localiza um trajeto específico pelo ID, carregando os dados completos de todas as entidades associadas.
     *
     * @param id identificador único do trajeto pesquisado
     * @return objeto TrajetoModel preenchido ou null se não for encontrado
     */
    public TrajetoModel buscar(int id) {
        String sql = BASE_SELECT + " WHERE t.id = ?;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairTrajeto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajeto: " + e.getMessage());
        }

        return null;
    }

    /**
     * Atualiza todas as informações e status de um trajeto existente no banco de dados.
     *
     * @param trajeto objeto contendo os novos dados do trajeto
     * @param id      identificador único do trajeto a ser atualizado
     * @return true se o registro foi atualizado com êxito, false em caso de falha
     */
    public boolean atualizar(TrajetoModel trajeto, int id) {
        String sql = """
                UPDATE trajeto
                SET id_motorista = ?,
                    id_caminhao = ?,
                    id_pecuarista = ?,
                    status = ?::status_trajeto,
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

    /**
     * Remove um registro de trajeto da base de dados com base no seu ID.
     *
     * @param id identificador único do trajeto a ser excluído
     * @return true se a exclusão for efetuada com sucesso, false caso ocorra erro
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM trajeto
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

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere um trajeto de forma simplificada apenas com os vínculos e documentos essenciais.
     */
    public boolean inserirSimples(Integer idMotorista, Integer idCaminhao, Integer idPecuarista, StatusTrajeto status, String numeroGta, String numeroNotaFiscal) {
        String sql = """
                INSERT INTO trajeto (id_motorista, id_caminhao, id_pecuarista, status, numero_gta, numero_nota_fiscal)
                VALUES (?, ?, ?, ?::status_trajeto, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idMotorista != null && idMotorista > 0) stmt.setInt(1, idMotorista); else stmt.setNull(1, Types.INTEGER);
            if (idCaminhao != null && idCaminhao > 0) stmt.setInt(2, idCaminhao); else stmt.setNull(2, Types.INTEGER);
            if (idPecuarista != null && idPecuarista > 0) stmt.setInt(3, idPecuarista); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, status != null ? status.name() : StatusTrajeto.EM_ANDAMENTO.name());
            stmt.setString(5, numeroGta);
            stmt.setString(6, numeroNotaFiscal);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir trajeto simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o status operacional de um trajeto.
     */
    public boolean atualizarStatus(int id, StatusTrajeto novoStatus) {
        String sql = "UPDATE trajeto SET status = ?::status_trajeto WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoStatus != null ? novoStatus.name() : StatusTrajeto.EM_ANDAMENTO.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o número da GTA de um trajeto.
     */
    public boolean atualizarGTA(int id, String novoGta) {
        String sql = "UPDATE trajeto SET numero_gta = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoGta != null ? novoGta.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar GTA do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o número da Nota Fiscal de um trajeto.
     */
    public boolean atualizarNotaFiscal(int id, String novaNotaFiscal) {
        String sql = "UPDATE trajeto SET numero_nota_fiscal = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaNotaFiscal != null ? novaNotaFiscal.trim() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar NF do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a quilometragem (saída e chegada) de um trajeto.
     */
    public boolean atualizarKm(int id, int kmSaida, int kmChegada) {
        String sql = "UPDATE trajeto SET km_saida = ?, km_chegada = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, kmSaida);
            stmt.setInt(2, kmChegada);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Km do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o motorista designado para um trajeto.
     */
    public boolean atualizarMotorista(int id, Integer idMotorista) {
        String sql = "UPDATE trajeto SET id_motorista = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idMotorista != null && idMotorista > 0) stmt.setInt(1, idMotorista); else stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar motorista do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o caminhão designado para um trajeto.
     */
    public boolean atualizarCaminhao(int id, Integer idCaminhao) {
        String sql = "UPDATE trajeto SET id_caminhao = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idCaminhao != null && idCaminhao > 0) stmt.setInt(1, idCaminhao); else stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar caminhão do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o pecuarista associado a um trajeto.
     */
    public boolean atualizarPecuarista(int id, Integer idPecuarista) {
        String sql = "UPDATE trajeto SET id_pecuarista = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idPecuarista != null && idPecuarista > 0) stmt.setInt(1, idPecuarista); else stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar pecuarista do trajeto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente o número do curral e nome do curraleiro.
     */
    public boolean atualizarCurral(int id, String numeroCurral, String curraleiro) {
        String sql = "UPDATE trajeto SET numero_curral = ?, nome_curraleiro = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroCurral);
            stmt.setString(2, curraleiro);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar curral do trajeto: " + e.getMessage());
            return false;
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca todos os trajetos realizados por um motorista específico.
     *
     * @param idMotorista identificador do motorista
     * @return lista de trajetos do motorista ordenados por data decrescente
     */
    public List<TrajetoModel> buscarPorMotorista(int idMotorista) {
        String sql = BASE_SELECT + " WHERE t.id_motorista = ? ORDER BY t.data_hora_inicio DESC, t.id DESC;";
        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idMotorista);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trajetos.add(extrairTrajeto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajetos por motorista: " + e.getMessage());
        }

        return trajetos;
    }

    /**
     * Busca todos os trajetos realizados utilizando um determinado caminhão.
     *
     * @param idCaminhao identificador do veículo
     * @return lista de trajetos vinculados ao caminhão ordenados por data decrescente
     */
    public List<TrajetoModel> buscarPorCaminhao(int idCaminhao) {
        String sql = BASE_SELECT + " WHERE t.id_caminhao = ? ORDER BY t.data_hora_inicio DESC, t.id DESC;";
        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idCaminhao);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trajetos.add(extrairTrajeto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajetos por caminhão: " + e.getMessage());
        }

        return trajetos;
    }

    /**
     * Busca todos os trajetos associados a um pecuarista específico.
     *
     * @param idPecuarista identificador do pecuarista
     * @return lista de trajetos vinculados ao produtor rural
     */
    public List<TrajetoModel> buscarPorPecuarista(int idPecuarista) {
        String sql = BASE_SELECT + " WHERE t.id_pecuarista = ? ORDER BY t.data_hora_inicio DESC, t.id DESC;";
        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idPecuarista);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trajetos.add(extrairTrajeto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajetos por pecuarista: " + e.getMessage());
        }

        return trajetos;
    }

    /**
     * Busca trajetos filtrados por seu status operacional atual.
     *
     * @param status status do trajeto (ex: PENDENTE, EM_TRANSITO, FINALIZADO)
     * @return lista de trajetos no status informado
     */
    public List<TrajetoModel> buscarPorStatus(StatusTrajeto status) {
        if (status == null) {
            return listar();
        }

        String sql = BASE_SELECT + " WHERE t.status = ?::status_trajeto ORDER BY t.data_hora_inicio DESC, t.id DESC;";
        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trajetos.add(extrairTrajeto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajetos por status: " + e.getMessage());
        }

        return trajetos;
    }

    /**
     * Localiza um trajeto pelo número da Guia de Trânsito Animal (GTA).
     *
     * @param numeroGta número da GTA a pesquisar
     * @return objeto TrajetoModel correspondente ou null se não encontrado
     */
    public TrajetoModel buscarPorGTA(String numeroGta) {
        if (numeroGta == null || numeroGta.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + " WHERE LOWER(t.numero_gta) = LOWER(?) LIMIT 1;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, numeroGta.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairTrajeto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajeto por GTA: " + e.getMessage());
        }

        return null;
    }

    /**
     * Localiza um trajeto pelo número da Nota Fiscal emitida.
     *
     * @param numeroNotaFiscal número fiscal a pesquisar
     * @return objeto TrajetoModel correspondente ou null se não encontrado
     */
    public TrajetoModel buscarPorNotaFiscal(String numeroNotaFiscal) {
        if (numeroNotaFiscal == null || numeroNotaFiscal.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + " WHERE LOWER(t.numero_nota_fiscal) = LOWER(?) LIMIT 1;";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, numeroNotaFiscal.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairTrajeto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajeto por Nota Fiscal: " + e.getMessage());
        }

        return null;
    }

    /**
     * Busca viagens que tiveram início dentro de um determinado intervalo de datas/horas.
     *
     * @param inicio data/hora inicial do intervalo
     * @param fim    data/hora final do intervalo
     * @return lista de trajetos iniciados no intervalo
     */
    public List<TrajetoModel> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = BASE_SELECT + " WHERE t.data_hora_inicio BETWEEN ? AND ? ORDER BY t.data_hora_inicio ASC;";
        List<TrajetoModel> trajetos = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setObject(1, inicio);
            stmt.setObject(2, fim);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trajetos.add(extrairTrajeto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar trajetos por período: " + e.getMessage());
        }

        return trajetos;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Converte o registro atual do ResultSet num objeto TrajetoModel completamente populado.
     *
     * @param rs ResultSet posicionado no registro a ser lido
     * @return instância de TrajetoModel completamente instanciada
     * @throws SQLException se houver falha de acesso aos dados do ResultSet
     */
    public TrajetoModel extrairTrajeto(ResultSet rs) throws SQLException {
        Date dataNascMotorista = rs.getDate("motorista_data_nascimento");
        LocalDate nascimentoMotorista = dataNascMotorista != null ? dataNascMotorista.toLocalDate() : null;

        MotoristaModel motorista = new MotoristaModel(
                rs.getInt("motorista_id"),
                new EmpresaModel(
                        rs.getInt("empresa_id"),
                        rs.getString("empresa_nome"),
                        rs.getString("empresa_cnpj"),
                        rs.getString("empresa_codigo")
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
        StatusTrajeto status = StatusTrajeto.from(statusStr);

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

    /**
     * Mapeia os dados do TrajetoModel para os parâmetros posicionais do PreparedStatement.
     *
     * @param stmt    PreparedStatement configurado com a consulta SQL
     * @param trajeto modelo contendo os dados do trajeto
     * @throws SQLException em caso de erro na associação dos parâmetros JDBC
     */
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

        if (trajeto.getPecuaristaModel() != null) {
            stmt.setInt(3, trajeto.getPecuaristaModel().getId());
        } else if (trajeto.getPecuarista() != null) {
            stmt.setInt(3, trajeto.getPecuarista().getId());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }

        stmt.setString(4, trajeto.getStatus() != null ? trajeto.getStatus().name() : StatusTrajeto.EM_ANDAMENTO.name());
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
