package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class CaminhaoMotoristaModel implements Model{

    private int id; //Id código unico
    private MotoristaModel motoristaModel; //Motorista da relação
    private CaminhaoModel caminhaoModel; //Caminhão da relação
    private LocalDate dataInicio; //Data de início da relação
    private LocalDate dataFim; //Data do fim da relação
    private boolean ativo; //Se a relação esta aiva

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

    public CaminhaoMotoristaModel() {
    }

    // getters e setters

    @Override
    public int getId() {
        return id;
    }

    //id imutável

    public MotoristaModel getMotorista() {
        return motoristaModel;
    }

    //O motorista da relação não pode mudar, caso o contrario a relação acaba

    public CaminhaoModel getCaminhao() {
        return caminhaoModel;
    }

    //O caminhão da relação não pode mudar

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    //A relação pode voltar a acontecer

    public boolean isAtivo() {
        return ativo;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
