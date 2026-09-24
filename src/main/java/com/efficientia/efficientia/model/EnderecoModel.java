package com.efficientia.efficientia.model;

/**
 * Modelo para Endereço.
 */
public class EnderecoModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private String cep;
    private String tipo;
    private String numero;
    private String rua;
    private String cidade;
    private String estado;
    private String pais;
    private String complemento;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
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

    // Construtor sem id (novo cadastro)
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

    // ==================== GETTERS E SETTERS ====================

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    // ==================== TO STRING ====================

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
