package com.efficientia.efficientia.model;

public class CargaEmbarque {
    private int id;
    private TrajetoCarga trajetoCarga; //Tabela interediaria do relacionamento
    private String tipo; //Pode ser marruco, Macho ou femea//ADD enum dps

    public CargaEmbarque(int id,
                         TrajetoCarga trajetoCarga,
                         String tipo) {
        this.id = id;
        this.trajetoCarga = trajetoCarga;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public TrajetoCarga getTrajetoCarga() {
        return trajetoCarga;
    }

    public String getTipo() {
        return tipo;
    }

    //Sem setters por não ter nada mutável
}
