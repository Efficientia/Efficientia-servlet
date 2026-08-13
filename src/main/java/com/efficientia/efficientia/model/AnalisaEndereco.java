package com.efficientia.efficientia.model;

public class AnalisaEndereco
{
    private int id;
    private Analista analista;
    private Endereco endereco;

    public AnalisaEndereco(int id,
                           Analista analista,
                           Endereco endereco) {
        this.id = id;
        this.analista = analista;
        this.endereco = endereco;
    }

    public AnalisaEndereco() {
    }

    public int getId() {
        return id;
    }

    public Analista getAnalista() {
        return analista;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    //adicionar setters

}
