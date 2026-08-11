package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class InfoDesembarque {
    private int id; //PK
    private Trajeto trajeto;
    private LocalDateTime horario;
    private String numeroCurral;
    private String nomeCurraleiro;
    private String nomeManobrista;
    private String assinaturaCurraleiro; //Provavelmente vai vurar hash dps
    private String assinaturaManobrista;
    private String assinaturaMotorista;

    public InfoDesembarque(int id,
                           Trajeto trajeto,
                           LocalDateTime horario,
                           String numeroCurral,
                           String nomeCurraleiro,
                           String nomeManobrista,
                           String assinaturaCurraleiro,
                           String assinaturaManobrista,
                           Motorista motorista ) { //Uso um objeto motorista para pegar sua assinatura
        this.id = id;
        this.trajeto = trajeto;
        this.horario = horario;
        this.numeroCurral = numeroCurral;
        this.nomeCurraleiro = nomeCurraleiro;
        this.nomeManobrista = nomeManobrista;
        this.assinaturaCurraleiro = assinaturaCurraleiro;
        this.assinaturaManobrista = assinaturaManobrista;
        this.assinaturaMotorista = motorista.getAssinatura();
    }

    public InfoDesembarque() {
    }

    public int getId() {
        return id;
    }

    public Trajeto getTrajeto() {
        return trajeto;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public String getNumeroCurral() {
        return numeroCurral;
    }

    public String getNomeCurraleiro() {
        return nomeCurraleiro;
    }

    public String getNomeManobrista() {
        return nomeManobrista;
    }

    public String getAssinaturaCurraleiro() {
        return assinaturaCurraleiro;
    }

    public String getAssinaturaManobrista() {
        return assinaturaManobrista;
    }

    public String getAssinaturaMotorista() {
        return assinaturaMotorista;
    }
}
//adicionar setters onde for necessario


