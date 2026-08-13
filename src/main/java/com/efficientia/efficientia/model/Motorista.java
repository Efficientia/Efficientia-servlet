package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class Motorista extends Usuario{

    public Motorista(String codigo,
                     String telefone,
                     String email,
                     String senha,
                     LocalDate dataNascimento,
                     String assinatura,
                     String nome) {
        super(codigo, telefone, email, senha, dataNascimento, assinatura, nome);
    }

    public Motorista() {
    }
}