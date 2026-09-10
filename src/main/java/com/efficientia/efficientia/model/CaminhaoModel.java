package com.efficientia.efficientia.model;

public class CaminhaoModel implements Model {

    private int id;
    private String placaCavalo;
    private String placaCarreta;
    private int capacidadeMaxima;

//Construtor com id do banco
    public CaminhaoModel(int id,
                         String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.id = id;
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

//Construtor sem id padrão
    public CaminhaoModel(String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

//Setters sem id
    public void setPlacaCavalo(String placaCavalo) {
        this.placaCavalo = placaCavalo;
    }

    public void setPlacaCarreta(String placaCarreta) {
        this.placaCarreta = placaCarreta;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public String getPlacaCavalo() {
        return placaCavalo;
    }

    public String getPlacaCarreta() {
        return placaCarreta;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    @Override
    public String toString() {
        return "CaminhaoModel{" +
                "id=" + id +
                ", placaCavalo='" + placaCavalo + '\'' +
                ", placaCarreta='" + placaCarreta + '\'' +
                ", capacidadeMaxima=" + capacidadeMaxima +
                '}';
    }
}
