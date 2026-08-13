package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class Trajeto {

    private int id;
    private Motorista motorista; //Motorista da relação
    private Caminhao caminhao; //Caminhão da relação

    private String status; //Qual o estado do trajeto

    private LocalDateTime dataHoraInicio; //inicio do trajeto
    private LocalDateTime dataHoraFim; //fim do trajeto

    private Integer kmSaida; //km de saida da estrada
    private Integer kmChegada; //km de chegada da estrada

    // getters e setters


    public Trajeto(int id,
                   Motorista motorista,
                   Caminhao caminhao,
                   String status,
                   LocalDateTime dataHoraInicio,
                   LocalDateTime dataHoraFim,
                   Integer kmSaida,
                   Integer kmChegada) {
        this.id = id;
        this.motorista = motorista;
        this.caminhao = caminhao;
        this.status = status;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.kmSaida = kmSaida;
        this.kmChegada = kmChegada;
    }

    public Trajeto() {
    }

    public int getId() {
        return id;
    }

    //Id imutável

    public Motorista getMotorista() {
        return motorista;
    }

    //Motorista imutável

    public Caminhao getCaminhao() {
        return caminhao;
    }

    //Caminhão imutável

    public String getStatus() {
        return status;
    }

    //Status vai mudando a medida que o trajeto vai se desenrolando

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    //Data e hora do inicio dão imutáveis

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }



    public Integer getKmSaida() {
        return kmSaida;
    }

    public Integer getKmChegada() {
        return kmChegada;
    }

//Adicionar setters
}
