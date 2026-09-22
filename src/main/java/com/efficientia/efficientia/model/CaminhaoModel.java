package com.efficientia.efficientia.model;

/**
 * Modelo para Caminhão (veículo de transporte).
 */
public class CaminhaoModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private String placaCavalo;
    private String placaCarreta;
    private int capacidadeMaxima;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public CaminhaoModel(int id,
                         String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.id = id;
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    // Construtor sem id (novo cadastro)
    public CaminhaoModel(String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    // ==================== GETTERS E SETTERS ====================

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlacaCavalo() {
        return placaCavalo;
    }

    public void setPlacaCavalo(String placaCavalo) {
        this.placaCavalo = placaCavalo;
    }

    public String getPlacaCarreta() {
        return placaCarreta;
    }

    public void setPlacaCarreta(String placaCarreta) {
        this.placaCarreta = placaCarreta;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    // ==================== TO STRING ====================

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
