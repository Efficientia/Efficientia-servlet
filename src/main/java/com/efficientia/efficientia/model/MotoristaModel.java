package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class MotoristaModel extends Usuario{

    public MotoristaModel(int id,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    public MotoristaModel() {
    }
}