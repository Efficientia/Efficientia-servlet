package com.efficientia.efficientia.DAO.interfaces;

import java.util.List;

public interface MotoristaDAO extends UsuarioDAO{
    void salvar(MotoristaDAO motoristaDAO);
    List<UsuarioDAO> listar();
    void deletar(int id);
}
