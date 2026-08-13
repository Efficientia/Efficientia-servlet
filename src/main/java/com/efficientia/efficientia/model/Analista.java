package com.efficientia.efficientia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Analista extends Usuario{

    private String cpf;

    public Analista(String codigo,
                    String telefone,
                    String email,
                    String senha,
                    LocalDate dataNascimento,
                    String assinatura,
                    String nome,
                    String cpf) {
        super(codigo, telefone, email, senha, dataNascimento, assinatura, nome);
        this.cpf = cpf;
    }

    public Analista(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}

