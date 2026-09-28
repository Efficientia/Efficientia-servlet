package com.efficientia.efficientia.model;

/**
 * Modelo representativo do Administrador do sistema Efficientia.
 *
 * O Administrador possui privilégios de gestão global no sistema,
 * sendo responsável pelo gerenciamento de acessos e configurações gerais.
 */
public class AdminModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do administrador (chave primária no banco de dados). */
    private int id;

    /** E-mail institucional/pessoal utilizado para login e notificações. */
    private String email;

    /** Senha de acesso criptografada/cadastrada para autenticação no sistema. */
    private String senha;

    /** Nome completo do administrador. */
    private String nome;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na recuperação e hidratação de dados vindos do banco de dados.
     *
     * @param id    identificador único gerado pelo banco de dados
     * @param email e-mail de acesso do administrador
     * @param senha senha de autenticação
     * @param nome  nome completo do administrador
     */
    public AdminModel(int id, String email, String senha, String nome) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    /**
     * Construtor sem ID.
     * Utilizado para criação de novos administradores antes da persistência no banco.
     *
     * @param email e-mail de acesso do administrador
     * @param senha senha de autenticação
     * @param nome  nome completo do administrador
     */
    public AdminModel(String email, String senha, String nome) {
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único do administrador.
     * @return ID numérico do administrador
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único do administrador.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o e-mail de acesso.
     * @return endereço de e-mail
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o e-mail de acesso.
     * @param email novo endereço de e-mail
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém a senha de autenticação.
     * @return senha do administrador
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha de autenticação.
     * @param senha nova senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Obtém o nome completo do administrador.
     * @return nome do administrador
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome completo do administrador.
     * @param nome novo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação em texto dos dados do administrador.
     * @return string formatada com os atributos do administrador
     */
    @Override
    public String toString() {
        return "AdminModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
