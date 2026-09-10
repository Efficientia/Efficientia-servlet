package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class TrajetoModel implements Model {

    private int id;
    private MotoristaModel motoristaModel;
    private CaminhaoModel caminhaoModel;
    private String status;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private Integer kmSaida;
    private Integer kmChegada;
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

//Construtor com id do banco
    public TrajetoModel(int id,
                        MotoristaModel motoristaModel,
                        CaminhaoModel caminhaoModel,
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
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
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
        if (this.motoristaModel != null) {
            this.assinaturaMotorista = this.motoristaModel.getAssinatura();
        }
    }

//Construtor sem id padrão
    public TrajetoModel(MotoristaModel motoristaModel,
                        CaminhaoModel caminhaoModel,
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
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
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
        if (this.motoristaModel != null) {
            this.assinaturaMotorista = this.motoristaModel.getAssinatura();
        }
    }

//Setters sem id
    public void setMotoristaModel(MotoristaModel motoristaModel) {
        this.motoristaModel = motoristaModel;
        if (motoristaModel != null) {
            this.assinaturaMotorista = motoristaModel.getAssinatura();
        }
    }

    public void setCaminhaoModel(CaminhaoModel caminhaoModel) {
        this.caminhaoModel = caminhaoModel;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public void setKmSaida(Integer kmSaida) {
        this.kmSaida = kmSaida;
    }

    public void setKmChegada(Integer kmChegada) {
        this.kmChegada = kmChegada;
    }

    public void setNomePecuarista(String nomePecuarista) {
        this.nomePecuarista = nomePecuarista;
    }

    public void setNumeroGTA(String numeroGTA) {
        this.numeroGTA = numeroGTA;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public void setHorarioEmbarque(LocalDateTime horarioEmbarque) {
        this.horarioEmbarque = horarioEmbarque;
    }

    public void setQtdMacho(int qtdMacho) {
        this.qtdMacho = qtdMacho;
    }

    public void setQtdFemea(int qtdFemea) {
        this.qtdFemea = qtdFemea;
    }

    public void setQtdMarruco(int qtdMarruco) {
        this.qtdMarruco = qtdMarruco;
    }

    public void setHorarioDesembarque(LocalDateTime horarioDesembarque) {
        this.horarioDesembarque = horarioDesembarque;
    }

    public void setNumeroCurral(String numeroCurral) {
        this.numeroCurral = numeroCurral;
    }

    public void setNomeCurraleiro(String nomeCurraleiro) {
        this.nomeCurraleiro = nomeCurraleiro;
    }

    public void setNomeManobrista(String nomeManobrista) {
        this.nomeManobrista = nomeManobrista;
    }

    public void setAssinaturaMotorista(String assinaturaMotorista) {
        this.assinaturaMotorista = assinaturaMotorista;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public MotoristaModel getMotoristaModel() {
        return motoristaModel;
    }

    public CaminhaoModel getCaminhaoModel() {
        return caminhaoModel;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public Integer getKmSaida() {
        return kmSaida;
    }

    public Integer getKmChegada() {
        return kmChegada;
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

    public int getQtdMacho() {
        return qtdMacho;
    }

    public int getQtdFemea() {
        return qtdFemea;
    }

    public int getQtdMarruco() {
        return qtdMarruco;
    }

    public LocalDateTime getHorarioDesembarque() {
        return horarioDesembarque;
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

    public String getAssinaturaMotorista() {
        return assinaturaMotorista;
    }

    @Override
    public String toString() {
        return "TrajetoModel{" +
                "id=" + id +
                ", motoristaModel=" + motoristaModel +
                ", caminhaoModel=" + caminhaoModel +
                ", status='" + status + '\'' +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                ", kmSaida=" + kmSaida +
                ", kmChegada=" + kmChegada +
                ", nomePecuarista='" + nomePecuarista + '\'' +
                ", numeroGTA='" + numeroGTA + '\'' +
                ", numeroNotaFiscal='" + numeroNotaFiscal + '\'' +
                ", horarioEmbarque=" + horarioEmbarque +
                ", qtdMacho=" + qtdMacho +
                ", qtdFemea=" + qtdFemea +
                ", qtdMarruco=" + qtdMarruco +
                ", horarioDesembarque=" + horarioDesembarque +
                ", numeroCurral='" + numeroCurral + '\'' +
                ", nomeCurraleiro='" + nomeCurraleiro + '\'' +
                ", nomeManobrista='" + nomeManobrista + '\'' +
                ", assinaturaMotorista='" + assinaturaMotorista + '\'' +
                '}';
    }
}
