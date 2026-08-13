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

    private String nomePecuarista;
    private String numeroGTA;
    private String numeroNotaFiscal;

    private LocalDateTime horarioEmbarque;

    private int qtdMacho;
    private int qtdFemea;
    private int qtdMarruco;

    private LocalDateTime horarioDesembarque;
    private String numeroCurral;
    private String nomeCurraleiro;
    private String nomeManobrista;
    private String assinaturaMotorista;


    // getters e setters


    public Trajeto(int id,
                   Motorista motorista,
                   Caminhao caminhao,
                   String status,
                   LocalDateTime dataHoraInicio,
                   LocalDateTime dataHoraFim,
                   Integer kmSaida,
                   Integer kmChegada,
                   String nomePecuarista,
                   String numeroGTA,
                   String numeroNotaFiscal,
                   LocalDateTime horarioEmbarque,
                   int qtdMacho,
                   int qtdFemea,
                   int qtdMarruco,
                   LocalDateTime horarioDesembarque,
                   String numeroCurral,
                   String nomeCurraleiro,
                   String nomeManobrista) {
        this.id = id;
        this.motorista = motorista;
        this.caminhao = caminhao;
        this.status = status;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.kmSaida = kmSaida;
        this.kmChegada = kmChegada;
        this.nomePecuarista = nomePecuarista;
        this.numeroGTA = numeroGTA;
        this.numeroNotaFiscal = numeroNotaFiscal;
        this.horarioEmbarque = horarioEmbarque;
        this.qtdMacho = qtdMacho;
        this.qtdFemea = qtdFemea;
        this.qtdMarruco = qtdMarruco;
        this.horarioDesembarque = horarioDesembarque;
        this.numeroCurral = numeroCurral;
        this.nomeCurraleiro = nomeCurraleiro;
        this.nomeManobrista = nomeManobrista;
        this.assinaturaMotorista = this.motorista.getAssinatura();
    }
}
