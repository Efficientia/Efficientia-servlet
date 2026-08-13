package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class ParadaImprevista {

    private int id;//PK da tabela
    private Trajeto trajeto; //Classe que reune as informações do documento
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String motivo; //Penso em fazer isso virar um arquivo txt

    public ParadaImprevista(int id,
                            Trajeto trajeto,
                            LocalDateTime dataHoraInicio,
                            LocalDateTime dataHoraFim,
                            String motivo) {
        this.id = id;
        this.trajeto = trajeto;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.motivo = motivo;
    }

    public ParadaImprevista() {
    }

    public int getId() {
        return id;
    }

    //PK não alteravel

    public Trajeto getTrajeto() {
        return trajeto;
    }

    //trajeto é unico

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    //Data e hora do inicio serão automaticos porem dataHoraFim pode ser adicionado posteriormente

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public String getMotivo() {
        return motivo;
    }


    //Adicionar setters

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
