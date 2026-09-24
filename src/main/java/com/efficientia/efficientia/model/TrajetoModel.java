package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class TrajetoModel implements Model {

    private int id;
    private MotoristaModel motoristaModel;
    private CaminhaoModel caminhaoModel;
    private StatusTrajeto status;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private int kmSaida;
    private int kmChegada;
    private PecuaristaModel pecuarista;
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
    private String assinaturaCurraleiro;
    private String assinaturaManobrista;
    private String assinaturaMotorista;

//Construtor com id do banco
    public TrajetoModel(int id,
                        MotoristaModel motoristaModel,
                        CaminhaoModel caminhaoModel,
                        StatusTrajeto status,
                        LocalDateTime dataHoraInicio,
                        LocalDateTime dataHoraFim,
                        Integer kmSaida,
                        Integer kmChegada,
                        PecuaristaModel pecuarista,
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
        this.pecuarista = pecuarista;
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
                        StatusTrajeto status,
                        LocalDateTime dataHoraInicio,
                        LocalDateTime dataHoraFim,
                        int kmSaida,
                        int kmChegada,
                        PecuaristaModel pecuarista,
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
        this.pecuarista = pecuarista;
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

    public void setStatus(StatusTrajeto status) {
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

    public void setNomePecuarista(PecuaristaModel nomePecuarista) {
        this.pecuarista = nomePecuarista;
    }

    public void setPecuarista(PecuaristaModel pecuarista) {
        this.pecuarista = pecuarista;
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

    public void setAssinaturaCurraleiro(String assinaturaCurraleiro) {
        this.assinaturaCurraleiro = assinaturaCurraleiro;
    }

    public void setAssinaturaManobrista(String assinaturaManobrista) {
        this.assinaturaManobrista = assinaturaManobrista;
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

    public StatusTrajeto getStatus() {
        return status;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public int getKmSaida() {
        return kmSaida;
    }

    public int getKmChegada() {
        return kmChegada;
    }

    public PecuaristaModel getPecuarista() {
        return pecuarista;
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

    public String getAssinaturaCurraleiro() {
        return assinaturaCurraleiro;
    }

    public String getAssinaturaManobrista() {
        return assinaturaManobrista;
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
                ", nomePecuarista='" + pecuarista + '\'' +
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
                ", assinaturaCurraleiro='" + assinaturaCurraleiro + '\'' +
                ", assinaturaManobrista='" + assinaturaManobrista + '\'' +
                ", assinaturaMotorista='" + assinaturaMotorista + '\'' +
                '}';
    }
}
