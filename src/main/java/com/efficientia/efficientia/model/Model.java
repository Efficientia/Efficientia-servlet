package com.efficientia.efficientia.model;

/**
 * Interface base para os modelos do sistema Efficientia.
 * Define o contrato padrão para entidades que possuem identificador único (ID).
 */
public interface Model {

    /**
     * Obtém o identificador único da entidade.
     * @return identificador numérico (chave primária) gerado pelo banco de dados.
     */
    int getId();
}
