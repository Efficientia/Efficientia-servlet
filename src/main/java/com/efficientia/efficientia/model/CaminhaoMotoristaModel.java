package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo associativo entre Caminhão e Motorista (com histórico de vigência).
 */
public class CaminhaoMotoristaModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private MotoristaModel motoristaModel;
    private CaminhaoModel caminhaoModel;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean ativo;

    // ==================== CONSTRUTORES ====================

    // Construtor com id e data de fim (banco)
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

    // Construtor com id sem data de fim (alocação em andamento)
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

    // Construtor sem id (nova alocação)
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

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MotoristaModel getMotorista() {
        return motoristaModel;
    }

    public MotoristaModel getMotoristaModel() {
        return motoristaModel;
    }

    public void setMotoristaModel(MotoristaModel motoristaModel) {
        this.motoristaModel = motoristaModel;
    }

    public CaminhaoModel getCaminhao() {
        return caminhaoModel;
    }

    public CaminhaoModel getCaminhaoModel() {
        return caminhaoModel;
    }

    public void setCaminhaoModel(CaminhaoModel caminhaoModel) {
        this.caminhaoModel = caminhaoModel;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // ==================== TO STRING ====================

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
