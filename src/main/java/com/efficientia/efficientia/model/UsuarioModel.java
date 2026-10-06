package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo abstrato base para os usuários do sistema Efficientia.
 *
 * Centraliza os atributos e comportamentos comuns compartilhados por atores
 * do sistema, tais como {@link MotoristaModel} e {@link AnalistaModel},
 * além de gerenciar a vinculação opcional a uma {@link EmpresaModel}.
 */
public abstract class UsuarioModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do usuário no banco de dados. */
    protected int id;

    /** Empresa à qual o usuário está vinculado (transportadora ou cliente). */
    protected EmpresaModel empresaModel;

    /** Nome completo do usuário. */
    protected String nome;

    /** Assinatura digitalizada ou identificador de rubrica do usuário. */
    protected String assinatura;

    /** Data de nascimento do usuário. */
    protected LocalDate dataNascimento;

    /** Senha de acesso ao sistema (armazenada de forma segura). */
    protected String senha;

    /** E-mail para autenticação e comunicações do sistema. */
    protected String email;

    /** Número de telefone ou celular para contato. */
    protected String telefone;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e vínculo de Empresa.
     * Utilizado na recuperação e hidratação de dados cadastrados a partir do banco de dados.
     *
     * @param id             identificador único gerado pelo banco de dados
     * @param empresaModel   empresa à qual o usuário pertence
     * @param nome           nome completo do usuário
     * @param assinatura     assinatura digital ou rubrica
     * @param dataNascimento data de nascimento
     * @param senha          senha de acesso ao sistema
     * @param email          endereço de e-mail
     * @param telefone       número de contato telefônico
     */
    public UsuarioModel(int id,
                        EmpresaModel empresaModel,
                        String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this.id = id;
        this.empresaModel = empresaModel;
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

    /**
     * Construtor sem ID e com vínculo de Empresa.
     * Utilizado para registrar um novo usuário corporativo antes da inserção no banco.
     *
     * @param empresaModel   empresa à qual o usuário será associado
     * @param nome           nome completo do usuário
     * @param assinatura     assinatura digital ou rubrica
     * @param dataNascimento data de nascimento
     * @param senha          senha de acesso ao sistema
     * @param email          endereço de e-mail
     * @param telefone       número de contato telefônico
     */
    public UsuarioModel(EmpresaModel empresaModel,
                        String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this.empresaModel = empresaModel;
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único do usuário.
     * @return ID numérico do usuário
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único do usuário.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém a empresa vinculada ao usuário.
     * @return objeto {@link EmpresaModel} associado
     */
    public EmpresaModel getEmpresaModel() {
        return empresaModel;
    }

    /**
     * Define a empresa vinculada ao usuário.
     * @param empresaModel objeto {@link EmpresaModel}
     */
    public void setEmpresaModel(EmpresaModel empresaModel) {
        this.empresaModel = empresaModel;
    }

    /**
     * Método utilitário alternativo para obter a empresa associada.
     * @return objeto {@link EmpresaModel} associado
     */
    public EmpresaModel getEmpresa() {
        return empresaModel;
    }

    /**
     * Obtém o nome completo do usuário.
     * @return nome do usuário
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome completo do usuário.
     * @param nome novo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém a assinatura digital do usuário.
     * @return string representativa da assinatura ou rubrica
     */
    public String getAssinatura() {
        return assinatura;
    }

    /**
     * Define a assinatura digital do usuário.
     * @param assinatura nova assinatura ou rubrica
     */
    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    /**
     * Obtém a data de nascimento do usuário.
     * @return data de nascimento (LocalDate)
     */
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define a data de nascimento do usuário.
     * @param dataNascimento nova data de nascimento
     */
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Obtém a senha de acesso ao sistema.
     * @return senha do usuário
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha de acesso ao sistema.
     * @param senha nova senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Obtém o e-mail cadastrado.
     * @return endereço de e-mail
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o e-mail cadastrado.
     * @param email novo endereço de e-mail
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém o número de telefone de contato.
     * @return telefone formatado ou numérico
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o número de telefone de contato.
     * @param telefone novo telefone
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do usuário.
     * @return string formatada com os atributos do usuário
     */
    @Override
    public String toString() {
        return "UsuarioModel{" +
                "id=" + id +
                ", empresaModel=" + empresaModel +
                ", nome='" + nome + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
