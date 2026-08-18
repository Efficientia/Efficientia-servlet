package com.efficientia.efficientia.DAO.interfaces;

import java.util.List;

public interface UsuarioDAO {
    void salvar(UsuarioDAO usuarioDAO);
    List<UsuarioDAO> listar();
    void deletar(int id);
}
