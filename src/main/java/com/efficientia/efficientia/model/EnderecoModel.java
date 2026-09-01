package com.efficientia.efficientia.model;

public class EnderecoModel implements Model{
    private int id;
    private String cep;
    private String tipo; //Virar enum
    private String numero;
    private String rua;
    private String cidade;
    private String estado;
    private String pais;
    private String complemento;

    public EnderecoModel(int id, String cep,
                         String tipo,
                         String numero,
                         String rua,
                         String cidade,
                         String estado,
                         String pais,
                         String complemento) {
        this.id = id;
        this.cep = cep;
        this.tipo = tipo;
        this.numero = numero;
        this.rua = rua;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.complemento = complemento;
    }

    public EnderecoModel() {
    }

    @Override
    public int getId() {
        return id;
    }

    public String getCep() {
        return cep;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

    public String getRua() {
        return rua;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getPais() {
        return pais;
    }

    public String getComplemento() {
        return complemento;
    }

    //Adicionar setter onde for necessario


    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
