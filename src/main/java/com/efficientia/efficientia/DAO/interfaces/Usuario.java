package com.efficientia.efficientia.DAO.interfaces;

import java.util.List;

public interface Usuario {
    void salvar(Usuario usuario);
    List<Usuario> listar();
    void deletar(int id);
}
