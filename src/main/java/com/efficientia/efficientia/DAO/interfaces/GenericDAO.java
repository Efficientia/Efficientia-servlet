package com.efficientia.efficientia.DAO.interfaces;

import java.util.List;
import com.efficientia.efficientia.model.Model;

// O limite "T extends Model" garante que apenas suas classes de domínio sejam aceitas.

public interface GenericDAO<T extends Model> {

    void salvar(T entidade);

    List<T> listar();

    void atualizar(T entidade);

    void deletar(int id);
}