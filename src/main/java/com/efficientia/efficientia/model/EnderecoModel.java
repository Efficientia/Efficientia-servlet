package com.efficientia.efficientia.model;

public class EnderecoModel implements Model {
    private int id;
    private String cep;
    private String tipo;
    private String numero;
    private String rua;
    private String cidade;
    private String estado;
    private String pais;
    private String complemento;

//Construtor com id do banco
    public EnderecoModel(int id,
                         String cep,
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

//Construtor sem id padrão
    public EnderecoModel(String cep,
                         String tipo,
                         String numero,
                         String rua,
                         String cidade,
                         String estado,
                         String pais,
                         String complemento) {
        this.cep = cep;
        this.tipo = tipo;
        this.numero = numero;
        this.rua = rua;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.complemento = complemento;
    }

//Setters sem id
    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

//Getters
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

    @Override
    public String toString() {
        return "EnderecoModel{" +
                "id=" + id +
                ", cep='" + cep + '\'' +
                ", tipo='" + tipo + '\'' +
                ", numero='" + numero + '\'' +
                ", rua='" + rua + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", pais='" + pais + '\'' +
                ", complemento='" + complemento + '\'' +
                '}';
    }
}
