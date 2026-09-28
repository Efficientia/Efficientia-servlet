package com.efficientia.efficientia.model;

/**
 * Interface base para todas as entidades de modelo do sistema Efficientia.
 *
 * Estabelece o contrato uniforme para classes que possuem identificador único
 * (chave primária) gerado e gerenciado pelo banco de dados relacional.
 */
public interface Model {

    /**
     * Obtém o identificador único da entidade no banco de dados.
     *
     * @return identificador numérico (chave primária)
     */
    int getId();
}
