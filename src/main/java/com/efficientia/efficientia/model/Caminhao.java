package com.efficientia.efficientia.model;

public class Caminhao {
    private int id; //Código único PK
    private int capacidadeMaxima; //Capacidade maxima de animais no caminhão
    private String placaCarreta; //Placa do caminhão
    private String placaCavalo;

    public Caminhao(String placaCavalo,
                    String placaCarreta,
                    int capacidadeMaxima,
                    int id) {
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
        this.id = id;
    }

    public Caminhao() {
    }

    public int getId() {
        return id;
    }

    //Id nao alterável

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    //Capacidade maxima inalterável

    public String getPlacaCarreta() {
        return placaCarreta;
    }

    //Placa carreta nao é alterável no sistema

    //Definir funcionamento de placa cavalo depois
    public String getPlacaCavalo() {
        return placaCavalo;
    }

    public void setPlacaCavalo(String placaCavalo) {
        this.placaCavalo = placaCavalo;
    }
}
