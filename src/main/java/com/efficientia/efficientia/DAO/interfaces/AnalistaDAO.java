package com.efficientia.efficientia.DAO.interfaces;

import java.util.List;

public interface AnalistaDAO extends UsuarioDAO{
    void salvar(AnalistaDAO analistaDAO);
    List<UsuarioDAO> listar();
    void deletar(int id);
}
