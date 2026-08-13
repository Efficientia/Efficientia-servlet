package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class Dirige {

    private int id; //Id código unico
    private Motorista motorista; //Motorista da relação
    private Caminhao caminhao; //Caminhão da relação
    private LocalDate dataInicio; //Data de início da relação
    private LocalDate dataFim; //Data do fim da relação
    private boolean ativo; //Se a relação esta aiva

    public Dirige(int id,
                  Motorista motorista,
                  Caminhao caminhao,
                  LocalDate dataInicio,
                  boolean ativo) {
        this.id = id;
        this.motorista = motorista;
        this.caminhao = caminhao;
        this.dataInicio = dataInicio;
        this.ativo = ativo;
    }

    public Dirige() {
    }

    // getters e setters


    public int getId() {
        return id;
    }

    //id imutável

    public Motorista getMotorista() {
        return motorista;
    }

    //O motorista da relação não pode mudar, caso o contrario a relação acaba

    public Caminhao getCaminhao() {
        return caminhao;
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

    //adicionar setters
}
