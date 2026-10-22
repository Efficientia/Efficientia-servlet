package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.EmpresaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Caminhão.
 *
 * Responsável por encapsular as operações de persistência e manipulação
 * de dados da tabela 'caminhao' no PostgreSQL, gerenciando conexões
 * via ConnectionFactory, realizando junções (JOIN) com 'empresa'
 * e garantindo fechamento de recursos com try-with-resources.
 */
public class CaminhaoDAO {

    // ==================== OPERAÇÕES CRUD ====================

    /**
     * Insere um novo registro de caminhão no banco de dados.
     *
     * @param caminhaoModel objeto contendo os dados do caminhão a ser inserido
     * @return true se o registro foi inserido com sucesso, false caso ocorra falha
     */
    public boolean inserir(CaminhaoModel caminhaoModel) {
        String sql = """
                INSERT INTO caminhao (
                    id_empresa,
                    placa_cavalo,
                    placa_carreta,
                    capacidade_maxima
                )
                VALUES (?, ?, ?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, caminhaoModel);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Caminhao: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos os caminhões cadastrados no banco de dados, incluindo dados da empresa associada via LEFT JOIN.
     *
     * @return lista contendo os caminhões encontrados ou lista vazia em caso de falha/ausência
     */
    public List<CaminhaoModel> listar() {
        String sql = """
                SELECT c.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM caminhao c
                LEFT JOIN empresa e ON e.id = c.id_empresa
                ORDER BY c.id;
                """;

        List<CaminhaoModel> caminhaoModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                caminhaoModels.add(extrairCaminhao(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Caminhao: " + e.getMessage());
        }

        return caminhaoModels;
    }

    /**
     * Atualiza os dados de um caminhão existente a partir do seu identificador.
     *
     * @param caminhaoModel objeto com os novos dados a serem gravados
     * @param id            identificador único do caminhão a ser atualizado
     * @return true se a alteração foi realizada com sucesso, false caso contrário
     */
    public boolean atualizar(CaminhaoModel caminhaoModel, int id) {
        String sql = """
                UPDATE caminhao
                SET id_empresa = ?,
                    placa_cavalo = ?,
                    placa_carreta = ?,
                    capacidade_maxima = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            preencherStatement(stmt, caminhaoModel);
            stmt.setInt(5, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Caminhao: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um registro de caminhão da base de dados com base no ID fornecido.
     *
     * @param id identificador único do caminhão a ser excluído
     * @return true se o registro foi removido com sucesso, false caso contrário
     */
    public boolean excluir(int id) {
        String sql = """
                DELETE FROM caminhao WHERE id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir Caminhao: " + e.getMessage());
            return false;
        }
    }

    /**
     * Localiza e retorna um caminhão específico a partir de seu identificador único.
     *
     * @param id identificador único do caminhão buscado
     * @return objeto CaminhaoModel correspondente ou null se não for encontrado
     */
    public CaminhaoModel buscar(int id) {
        String sql = """
                SELECT c.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM caminhao c
                LEFT JOIN empresa e ON e.id = c.id_empresa
                WHERE c.id = ?;
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairCaminhao(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Caminhao: " + e.getMessage());
        }

        return null;
    }

    // ==================== INSERÇÃO E ATUALIZAÇÃO ESPECÍFICAS ====================

    /**
     * Insere um caminhão de forma simplificada com dados essenciais.
     */
    public boolean inserirSimples(String placaCavalo, String placaCarreta, int capacidadeMaxima, Integer idEmpresa) {
        String sql = """
                INSERT INTO caminhao (id_empresa, placa_cavalo, placa_carreta, capacidade_maxima)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idEmpresa != null && idEmpresa > 0) {
                stmt.setInt(1, idEmpresa);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, placaCavalo);
            stmt.setString(3, placaCarreta);
            stmt.setInt(4, capacidadeMaxima);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir caminhão simples: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a placa do cavalo mecânico.
     */
    public boolean atualizarPlacaCavalo(int id, String novaPlaca) {
        String sql = "UPDATE caminhao SET placa_cavalo = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaPlaca != null ? novaPlaca.trim().toUpperCase() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar placa cavalo do caminhão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a placa da carreta.
     */
    public boolean atualizarPlacaCarreta(int id, String novaPlaca) {
        String sql = "UPDATE caminhao SET placa_carreta = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaPlaca != null ? novaPlaca.trim().toUpperCase() : null);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar placa carreta do caminhão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a capacidade máxima do caminhão.
     */
    public boolean atualizarCapacidadeMaxima(int id, int novaCapacidade) {
        String sql = "UPDATE caminhao SET capacidade_maxima = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, novaCapacidade);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar capacidade do caminhão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza especificamente a empresa vinculada ao caminhão.
     */
    public boolean atualizarEmpresa(int id, Integer idEmpresa) {
        String sql = "UPDATE caminhao SET id_empresa = ? WHERE id = ?;";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (idEmpresa != null && idEmpresa > 0) {
                stmt.setInt(1, idEmpresa);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar empresa do caminhão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca caminhões por placa (pesquisando tanto em placa_cavalo quanto em placa_carreta),
     * tolerante a hífens e maiúsculas/minúsculas.
     *
     * @param placa termo ou placa a ser pesquisada
     * @return lista de caminhões correspondentes
     */
    public List<CaminhaoModel> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            return listar();
        }

        // Remove hífens e espaços para cobrir tanto ABC-1234 quanto ABC1234 e padrão Mercosul
        String placaLimpa = placa.replace("-", "").trim().toUpperCase();

        String sql = """
                SELECT c.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM caminhao c
                LEFT JOIN empresa e ON e.id = c.id_empresa
                WHERE REPLACE(UPPER(c.placa_cavalo), '-', '') LIKE ?
                   OR REPLACE(UPPER(c.placa_carreta), '-', '') LIKE ?
                ORDER BY c.id ASC;
                """;

        List<CaminhaoModel> caminhaoModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            String param = "%" + placaLimpa + "%";
            stmt.setString(1, param);
            stmt.setString(2, param);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caminhaoModels.add(extrairCaminhao(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar caminhões por placa: " + e.getMessage());
        }

        return caminhaoModels;
    }

    /**
     * Localiza todos os caminhões pertencentes a uma empresa específica.
     *
     * @param idEmpresa ID da empresa associada
     * @return lista de caminhões pertencentes à empresa informada
     */
    public List<CaminhaoModel> buscarPorEmpresa(int idEmpresa) {
        String sql = """
                SELECT c.*,
                       e.id AS empresa_id,
                       e.nome AS empresa_nome,
                       e.cnpj AS empresa_cnpj,
                       e.codigo AS empresa_codigo
                FROM caminhao c
                LEFT JOIN empresa e ON e.id = c.id_empresa
                WHERE c.id_empresa = ?
                ORDER BY c.id ASC;
                """;

        List<CaminhaoModel> caminhaoModels = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idEmpresa);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caminhaoModels.add(extrairCaminhao(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar caminhões por empresa: " + e.getMessage());
        }

        return caminhaoModels;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Constrói uma instância de CaminhaoModel a partir do registro atual do ResultSet,
     * incluindo o EmpresaModel caso a chave estrangeira esteja presente.
     *
     * @param rs ResultSet posicionado no registro atual
     * @return objeto CaminhaoModel preenchido
     * @throws SQLException em caso de falha de leitura dos dados
     */
    private CaminhaoModel extrairCaminhao(ResultSet rs) throws SQLException {
        EmpresaModel empresaModel = null;
        int idEmpresa = rs.getInt("empresa_id");
        if (!rs.wasNull()) {
            empresaModel = new EmpresaModel(
                    idEmpresa,
                    rs.getString("empresa_nome"),
                    rs.getString("empresa_cnpj"),
                    rs.getString("empresa_codigo")
            );
        }

        return new CaminhaoModel(
                rs.getInt("id"),
                empresaModel,
                rs.getString("placa_cavalo"),
                rs.getString("placa_carreta"),
                rs.getInt("capacidade_maxima")
        );
    }

    /**
     * Mapeia os dados do modelo nos parâmetros posicionais do PreparedStatement,
     * incluindo o identificador da empresa.
     *
     * @param stmt          statement preparado para receber os parâmetros
     * @param caminhaoModel objeto com os dados a serem vinculados
     * @throws SQLException se ocorrer falha ao atribuir os valores no JDBC
     */
    private void preencherStatement(
            PreparedStatement stmt,
            CaminhaoModel caminhaoModel
    ) throws SQLException {
        if (caminhaoModel.getEmpresaModel() != null && caminhaoModel.getEmpresaModel().getId() > 0) {
            stmt.setInt(1, caminhaoModel.getEmpresaModel().getId());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }
        stmt.setString(2, caminhaoModel.getPlacaCavalo());
        stmt.setString(3, caminhaoModel.getPlacaCarreta());
        stmt.setInt(4, caminhaoModel.getCapacidadeMaxima());
    }
}
