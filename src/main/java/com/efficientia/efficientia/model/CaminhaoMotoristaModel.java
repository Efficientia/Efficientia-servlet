package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo associativo entre Caminhão e Motorista no sistema Efficientia.
 *
 * Registra a atribuição operacional e o histórico de vigência de qual
 * {@link MotoristaModel} está habilitado e designado para conduzir qual
 * {@link CaminhaoModel}, controlando o período e se a designação está ativa.
 */
public class CaminhaoMotoristaModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do registro associativo (chave primária no banco). */
    private int id;

    /** Motorista designado para o veículo. */
    private MotoristaModel motoristaModel;

    /** Caminhão atribuído ao motorista. */
    private CaminhaoModel caminhaoModel;

    /** Data de início da vigência da designação. */
    private LocalDate dataInicio;

    /** Data de término da vigência da designação (nulo se ainda em aberto). */
    private LocalDate dataFim;

    /** Indicador booleano que expressa se a atribuição está atualmente ativa. */
    private boolean ativo;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e data de término de vigência.
     * Utilizado para reconstituição de alocações históricas ou finalizadas vindas do banco.
     *
     * @param id             identificador único do registro
     * @param motoristaModel motorista designado
     * @param caminhaoModel  caminhão designado
     * @param dataInicio     data de início da atribuição
     * @param dataFim        data de encerramento da atribuição
     * @param ativo          status ativo (true) ou inativo (false)
     */
    public CaminhaoMotoristaModel(int id,
                                  MotoristaModel motoristaModel,
                                  CaminhaoModel caminhaoModel,
                                  LocalDate dataInicio,
                                  LocalDate dataFim,
                                  boolean ativo) {
        this.id = id;
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativo = ativo;
    }

    /**
     * Construtor com ID e sem data de término de vigência.
     * Utilizado para alocações ativas carregadas do banco sem data de término definida.
     *
     * @param id             identificador único do registro
     * @param motoristaModel motorista designado
     * @param caminhaoModel  caminhão designado
     * @param dataInicio     data de início da atribuição
     * @param ativo          status ativo (true) ou inativo (false)
     */
    public CaminhaoMotoristaModel(int id,
                                  MotoristaModel motoristaModel,
                                  CaminhaoModel caminhaoModel,
                                  LocalDate dataInicio,
                                  boolean ativo) {
        this.id = id;
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
        this.dataInicio = dataInicio;
        this.ativo = ativo;
    }

    /**
     * Construtor sem ID.
     * Utilizado no momento da criação de uma nova atribuição motorista-caminhão.
     *
     * @param motoristaModel motorista designado
     * @param caminhaoModel  caminhão designado
     * @param dataInicio     data de início da atribuição
     * @param ativo          status ativo (true) ou inativo (false)
     */
    public CaminhaoMotoristaModel(MotoristaModel motoristaModel,
                                  CaminhaoModel caminhaoModel,
                                  LocalDate dataInicio,
                                  boolean ativo) {
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
        this.dataInicio = dataInicio;
        this.ativo = ativo;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da associação.
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da associação.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método utilitário alternativo para obter o motorista designado.
     * @return objeto {@link MotoristaModel} associado
     */
    public MotoristaModel getMotorista() {
        return motoristaModel;
    }

    /**
     * Obtém o motorista designado.
     * @return objeto {@link MotoristaModel} associado
     */
    public MotoristaModel getMotoristaModel() {
        return motoristaModel;
    }

    /**
     * Define o motorista designado.
     * @param motoristaModel novo motorista designado
     */
    public void setMotoristaModel(MotoristaModel motoristaModel) {
        this.motoristaModel = motoristaModel;
    }

    /**
     * Método utilitário alternativo para obter o caminhão designado.
     * @return objeto {@link CaminhaoModel} associado
     */
    public CaminhaoModel getCaminhao() {
        return caminhaoModel;
    }

    /**
     * Obtém o caminhão designado.
     * @return objeto {@link CaminhaoModel} associado
     */
    public CaminhaoModel getCaminhaoModel() {
        return caminhaoModel;
    }

    /**
     * Define o caminhão designado.
     * @param caminhaoModel novo caminhão designado
     */
    public void setCaminhaoModel(CaminhaoModel caminhaoModel) {
        this.caminhaoModel = caminhaoModel;
    }

    /**
     * Obtém a data de início da atribuição.
     * @return data de início (LocalDate)
     */
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    /**
     * Define a data de início da atribuição.
     * @param dataInicio nova data de início
     */
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * Obtém a data de encerramento da atribuição.
     * @return data de encerramento ou null caso ainda ativa
     */
    public LocalDate getDataFim() {
        return dataFim;
    }

    /**
     * Define a data de encerramento da atribuição.
     * @param dataFim nova data de término
     */
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    /**
     * Verifica se a atribuição está atualmente ativa.
     * @return true se ativo, false caso contrário
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Define o status de ativação da atribuição.
     * @param ativo novo estado (true para ativo, false para inativo)
     */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual da associação caminhão-motorista.
     * @return string formatada contendo os atributos do vínculo
     */
    @Override
    public String toString() {
        return "CaminhaoMotoristaModel{" +
                "id=" + id +
                ", motoristaModel=" + motoristaModel +
                ", caminhaoModel=" + caminhaoModel +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", ativo=" + ativo +
                '}';
    }
}
