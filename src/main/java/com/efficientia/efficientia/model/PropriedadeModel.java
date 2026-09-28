package com.efficientia.efficientia.model;

/**
 * Modelo representativo de uma Propriedade Rural (fazenda/sítio) no sistema Efficientia.
 *
 * Representa o local de origem de onde os animais são embarcados para transporte.
 * Cada propriedade pertence a um {@link PecuaristaModel} e possui um {@link EnderecoModel} associado.
 */
public class PropriedadeModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único da propriedade rural (chave primária no banco de dados). */
    private int id;

    /** Pecuarista proprietário ou responsável pela propriedade rural. */
    private PecuaristaModel pecuaristaModel;

    /** Endereço físico e geográfico onde a propriedade está localizada. */
    private EnderecoModel enderecoModel;

    /** Nome comercial ou denominação da fazenda / sítio / propriedade. */
    private String nome;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na recuperação e hidratação da propriedade a partir do banco de dados.
     *
     * @param id              identificador único da propriedade
     * @param pecuaristaModel pecuarista proprietário
     * @param enderecoModel   endereço da propriedade
     * @param nome            nome da fazenda ou propriedade
     */
    public PropriedadeModel(int id,
                            PecuaristaModel pecuaristaModel,
                            EnderecoModel enderecoModel,
                            String nome) {
        this.id = id;
        this.pecuaristaModel = pecuaristaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    /**
     * Construtor sem ID.
     * Utilizado no cadastro de uma nova propriedade antes da persistência no banco.
     *
     * @param pecuaristaModel pecuarista proprietário
     * @param enderecoModel   endereço da propriedade
     * @param nome            nome da fazenda ou propriedade
     */
    public PropriedadeModel(PecuaristaModel pecuaristaModel,
                            EnderecoModel enderecoModel,
                            String nome) {
        this.pecuaristaModel = pecuaristaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da propriedade rural.
     * @return ID numérico da propriedade
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da propriedade rural.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o pecuarista proprietário da fazenda.
     * @return objeto {@link PecuaristaModel} associado
     */
    public PecuaristaModel getPecuaristaModel() {
        return pecuaristaModel;
    }

    /**
     * Define o pecuarista proprietário da fazenda.
     * @param pecuaristaModel objeto {@link PecuaristaModel}
     */
    public void setPecuaristaModel(PecuaristaModel pecuaristaModel) {
        this.pecuaristaModel = pecuaristaModel;
    }

    /**
     * Obtém o endereço da propriedade rural.
     * @return objeto {@link EnderecoModel} associado
     */
    public EnderecoModel getEnderecoModel() {
        return enderecoModel;
    }

    /**
     * Define o endereço da propriedade rural.
     * @param enderecoModel objeto {@link EnderecoModel}
     */
    public void setEnderecoModel(EnderecoModel enderecoModel) {
        this.enderecoModel = enderecoModel;
    }

    /**
     * Obtém o nome da fazenda ou propriedade rural.
     * @return nome da propriedade
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da fazenda ou propriedade rural.
     * @param nome novo nome da propriedade
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados da propriedade rural.
     * @return string formatada contendo os atributos da propriedade
     */
    @Override
    public String toString() {
        return "PropriedadeModel{" +
                "id=" + id +
                ", pecuaristaModel=" + pecuaristaModel +
                ", enderecoModel=" + enderecoModel +
                ", nome='" + nome + '\'' +
                '}';
    }
}
