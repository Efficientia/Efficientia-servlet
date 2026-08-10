package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class InfoInicial {
    private int id; //PK
    private Trajeto trajeto; //Classe que reune pro documento
    private String nomePecuarista;
    private String numeroGTA; //Prefiro tratar como String por não envolver contas
    private String numeroNotaFiscal;//O mesmo
    private LocalDateTime horarioEmbarque; //Data e hora do embarque

    public InfoInicial(int id,
                       Trajeto trajeto,
                       String nomePecuarista,
                       String numeroGTA,
                       String numeroNotaFiscal,
                       LocalDateTime horarioEmbarque) {
        this.id = id;
        this.trajeto = trajeto;
        this.nomePecuarista = nomePecuarista;
        this.numeroGTA = numeroGTA;
        this.numeroNotaFiscal = numeroNotaFiscal;
        this.horarioEmbarque = horarioEmbarque;
    }

    public InfoInicial() {
    }

    public int getId() {
        return id;
    }

    public Trajeto getTrajeto() {
        return trajeto;
    }

    public String getNomePecuarista() {
        return nomePecuarista;
    }

    public String getNumeroGTA() {
        return numeroGTA;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public LocalDateTime getHorarioEmbarque() {
        return horarioEmbarque;
    }

    //Setters no geral sao imutaveis mas verei depois
}
