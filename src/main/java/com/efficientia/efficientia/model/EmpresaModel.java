package com.efficientia.efficientia.model;

/**
 * Modelo representativo de uma Empresa no sistema Efficientia.
 *
 * Representa entidades jurídicas como transportadoras parceiras ou empresas
 * contratantes envolvidas na gestão e operação logística de transporte pecuário.
 */
public class EmpresaModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único da empresa (chave primária no banco de dados). */
    private int id;

    /** Nome fantasia ou razão social da empresa. */
    private String nome;

    /** Cadastro Nacional da Pessoa Jurídica (CNPJ) formatado ou numérico. */
    private String cnpj;

    /** Código de registro ou identificador corporativo da empresa. */
    private String codigo;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e código.
     * Utilizado na recuperação e hidratação de dados vindos do banco de dados.
     *
     * @param id     identificador único da empresa
     * @param nome   razão social ou nome fantasia da empresa
     * @param cnpj   Cadastro Nacional da Pessoa Jurídica (CNPJ)
     * @param codigo código identificador da empresa vindo do banco de dados
     */
    public EmpresaModel(int id, String nome, String cnpj, String codigo) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.codigo = codigo;
    }

    /**
     * Construtor com ID (caso a consulta não selecione o código).
     *
     * @param id   identificador único da empresa
     * @param nome razão social ou nome fantasia da empresa
     * @param cnpj Cadastro Nacional da Pessoa Jurídica (CNPJ)
     */
    public EmpresaModel(int id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    /**
     * Construtor sem ID (utilizado no cadastro de nova empresa pela aplicação, onde o código não é inserido).
     *
     * @param nome razão social ou nome fantasia da empresa
     * @param cnpj Cadastro Nacional da Pessoa Jurídica (CNPJ)
     */
    public EmpresaModel(String nome, String cnpj) {
        this.nome = nome;
        this.cnpj = cnpj;
    }


    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da empresa.
     * @return ID numérico da empresa
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da empresa.
     * @param id ID numérico da empresa
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém a razão social ou nome fantasia da empresa.
     * @return nome da empresa
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define a razão social ou nome fantasia da empresa.
     * @param nome novo nome da empresa
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o CNPJ da empresa.
     * @return número do CNPJ
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * Define o CNPJ da empresa.
     * @param cnpj novo número do CNPJ
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    /**
     * Obtém o código identificador da empresa.
     * @return código da empresa
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Define o código identificador da empresa.
     * @param codigo novo código da empresa
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados da empresa.
     * @return string formatada contendo os atributos da empresa
     */
    @Override
    public String toString() {
        return "EmpresaModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", codigo='" + codigo + '\'' +
                '}';
    }
}
