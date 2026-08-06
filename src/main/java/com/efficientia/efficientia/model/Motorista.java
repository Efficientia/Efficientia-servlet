package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class Motorista {

    private String codigo; //Código do motorista PK
    private String nome;
    private String assinatura; //Hash da assinatura
    private LocalDate dataNascimento;
    private String senha; //Codigo de acesso no sistema
    private String email;
    private String telefone;

    public Motorista(String codigo,
                     String nome,
                     String assinatura,
                     LocalDate dataNascimento,
                     String senha,
                     String email,
                     String telefone) {
        this.codigo = codigo;
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

    public Motorista() {
    }

    public String getCodigo() {
        return codigo;
    }

    //Set codigo não escolhido por por alterar PK

    public String getNome() {
        return nome;
    }

    //Set nome não escolhido por não ser variável

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    //Assinatura pode mudar de hash ou o arquivo em si

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    //Data de nascimento é imutável

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    //Senha pode mudar caso esquecida

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    //Email e telefone não serão alteráveis por serem da empresa


}