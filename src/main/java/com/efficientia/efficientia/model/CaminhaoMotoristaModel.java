package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class CaminhaoMotoristaModel implements Model {

    private int id;
    private MotoristaModel motoristaModel;
    private CaminhaoModel caminhaoModel;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean ativo;

//Construtor com id do banco
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

//Construtor sem id padrão
    public CaminhaoMotoristaModel(MotoristaModel motoristaModel,
                                  CaminhaoModel caminhaoModel,
                                  LocalDate dataInicio,
                                  boolean ativo) {
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
        this.dataInicio = dataInicio;
        this.ativo = ativo;
    }

//Setters sem id
    public void setMotoristaModel(MotoristaModel motoristaModel) {
        this.motoristaModel = motoristaModel;
    }

    public void setCaminhaoModel(CaminhaoModel caminhaoModel) {
        this.caminhaoModel = caminhaoModel;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public MotoristaModel getMotorista() {
        return motoristaModel;
    }

    public CaminhaoModel getCaminhao() {
        return caminhaoModel;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public boolean isAtivo() {
        return ativo;
    }

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
