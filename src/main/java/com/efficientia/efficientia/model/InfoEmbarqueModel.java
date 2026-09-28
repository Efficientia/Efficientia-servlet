package com.efficientia.efficientia.model;

/**
 * Modelo representativo das Informações de Embarque no sistema Efficientia.
 *
 * Armazena dados complementares e documentais associados ao processo de embarque
 * de animais em um determinado {@link TrajetoModel}.
 */
public class InfoEmbarqueModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único da informação de embarque (chave primária no banco). */
    private int id;

    /** Nome descritivo ou título do registro/documento de embarque. */
    private String nome;

    /** Trajeto / viagem ao qual este registro de embarque pertence. */
    private TrajetoModel trajetoModel;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na reconstituição da informação de embarque a partir do banco de dados.
     *
     * @param id           identificador único do registro
     * @param nome         título ou descrição da informação de embarque
     * @param trajetoModel trajeto ao qual está associado
     */
    public InfoEmbarqueModel(int id,
                             String nome,
                             TrajetoModel trajetoModel) {
        this.id = id;
        this.nome = nome;
        this.trajetoModel = trajetoModel;
    }

    /**
     * Construtor sem ID.
     * Utilizado no momento da criação de um novo registro antes da persistência no banco.
     *
     * @param nome         título ou descrição da informação de embarque
     * @param trajetoModel trajeto ao qual está associado
     */
    public InfoEmbarqueModel(String nome,
                             TrajetoModel trajetoModel) {
        this.nome = nome;
        this.trajetoModel = trajetoModel;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da informação de embarque.
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da informação de embarque.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém a denominação/nome da informação de embarque.
     * @return nome do registro
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define a denominação/nome da informação de embarque.
     * @param nome novo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Método utilitário alternativo para obter o trajeto associado.
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    /**
     * Obtém o trajeto associado ao registro de embarque.
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajetoModel() {
        return trajetoModel;
    }

    /**
     * Define o trajeto associado ao registro de embarque.
     * @param trajetoModel novo trajeto vinculado
     */
    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados da informação de embarque.
     * @return string formatada contendo os atributos do registro
     */
    @Override
    public String toString() {
        return "InfoEmbarqueModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", trajetoModel=" + trajetoModel +
                '}';
    }
}
