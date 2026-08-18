package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class AnalistaModel extends UsuarioModel{

    private String codigo;

    public AnalistaModel(int id,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);

        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}

