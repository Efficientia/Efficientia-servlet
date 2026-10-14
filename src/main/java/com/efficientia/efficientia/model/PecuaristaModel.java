package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo representativo do Pecuarista (proprietário rural / produtor de gado).
 *
 * Representa o produtor rural responsável pelas fazendas de origem do gado transportado,
 * atuando como cliente/parceiro da cadeia logística e emissor das documentações de embarque.
 */
public class PecuaristaModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do pecuarista (chave primária no banco de dados). */
    private int id;

    /** Cadastro de Pessoa Física (CPF) do pecuarista. */
    private String cpf;

    /** Assinatura digitalizada ou rubrica do pecuarista para validação de embarques. */
    private String assinatura;

    /** Data de nascimento do pecuarista. */
    private LocalDate dataNascimento;

    /** Nome completo do pecuarista ou produtor rural. */
    private String nome;

    /** Senha de acesso ao portal/sistema. */
    private String senha;

    /** Endereço de e-mail para contato e login. */
    private String email;

    /** Número de telefone ou celular para contato comercial e operacional. */
    private String telefone;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na recuperação e hidratação dos dados do pecuarista a partir do banco de dados.
     *
     * @param id             identificador único gerado pelo banco
     * @param cpf            número do CPF (máximo de 11 dígitos)
     * @param assinatura     assinatura digitalizada ou identificador de rubrica
     * @param dataNascimento data de nascimento
     * @param nome           nome completo do pecuarista
     * @param senha          senha de acesso ao sistema
     * @param email          endereço de e-mail
     * @param telefone       número de telefone de contato (máximo de 11 dígitos)
     */
    public PecuaristaModel(int id,
                           String cpf,
                           String assinatura,
                           LocalDate dataNascimento,
                           String nome,
                           String senha,
                           String email,
                           String telefone) {
        this.id = id;
        this.cpf = normalizarCpf(cpf);
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.telefone = normalizarTelefone(telefone);
    }

    /**
     * Construtor completo com ID (sem assinatura).
     * Utilizado na atualização/edição de dados cadastrais pelo site.
     *
     * @param id             identificador único gerado pelo banco
     * @param cpf            número do CPF (máximo de 11 dígitos)
     * @param dataNascimento data de nascimento
     * @param nome           nome completo do pecuarista
     * @param senha          senha de acesso ao sistema
     * @param email          endereço de e-mail
     * @param telefone       número de telefone de contato (máximo de 11 dígitos)
     */
    public PecuaristaModel(int id,
                           String cpf,
                           LocalDate dataNascimento,
                           String nome,
                           String senha,
                           String email,
                           String telefone) {
        this(id, cpf, null, dataNascimento, nome, senha, email, telefone);
    }

    /**
     * Construtor sem ID (com assinatura).
     *
     * @param cpf            número do CPF (máximo de 11 dígitos)
     * @param assinatura     assinatura digitalizada ou identificador de rubrica
     * @param dataNascimento data de nascimento
     * @param nome           nome completo do pecuarista
     * @param senha          senha de acesso ao sistema
     * @param email          endereço de e-mail
     * @param telefone       número de telefone de contato (máximo de 11 dígitos)
     */
    public PecuaristaModel(String cpf,
                           String assinatura,
                           LocalDate dataNascimento,
                           String nome,
                           String senha,
                           String email,
                           String telefone) {
        this.cpf = normalizarCpf(cpf);
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.telefone = normalizarTelefone(telefone);
    }

    /**
     * Construtor sem ID (sem assinatura).
     * Utilizado no cadastro de um novo pecuarista pelo site (onde o site não cadastra assinatura).
     *
     * @param cpf            número do CPF (máximo de 11 dígitos)
     * @param dataNascimento data de nascimento
     * @param nome           nome completo do pecuarista
     * @param senha          senha de acesso ao sistema
     * @param email          endereço de e-mail
     * @param telefone       número de telefone de contato (máximo de 11 dígitos)
     */
    public PecuaristaModel(String cpf,
                           LocalDate dataNascimento,
                           String nome,
                           String senha,
                           String email,
                           String telefone) {
        this(cpf, null, dataNascimento, nome, senha, email, telefone);
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único do pecuarista.
     *
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único do pecuarista.
     *
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o CPF do pecuarista.
     *
     * @return número do CPF
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do pecuarista.
     *
     * @param cpf novo número de CPF
     */
    public void setCpf(String cpf) {
        this.cpf = normalizarCpf(cpf);
    }

    /**
     * Obtém a assinatura digitalizada do pecuarista.
     *
     * @return representação da assinatura ou rubrica
     */
    public String getAssinatura() {
        return assinatura;
    }

    /**
     * Define a assinatura digitalizada do pecuarista.
     *
     * @param assinatura nova assinatura ou rubrica
     */
    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    /**
     * Obtém a data de nascimento do pecuarista.
     *
     * @return data de nascimento
     */
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define a data de nascimento do pecuarista.
     *
     * @param dataNascimento nova data de nascimento
     */
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Obtém o nome completo do pecuarista.
     *
     * @return nome do pecuarista
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome completo do pecuarista.
     *
     * @param nome novo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém a senha de acesso ao sistema.
     *
     * @return senha do pecuarista
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha de acesso ao sistema.
     *
     * @param senha nova senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Obtém o endereço de e-mail do pecuarista.
     *
     * @return e-mail cadastrado
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o endereço de e-mail do pecuarista.
     *
     * @param email novo e-mail
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém o telefone de contato.
     *
     * @return número de telefone
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone de contato.
     *
     * @param telefone novo número de telefone
     */
    public void setTelefone(String telefone) {
        this.telefone = normalizarTelefone(telefone);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Normaliza defensivamente o CPF para manter consistência com limites de colunas.
     *
     * @param cpf valor original informado
     * @return "nulo" caso exceda 11 caracteres, ou o próprio CPF
     */
    private String normalizarCpf(String cpf) {
        if (cpf != null && cpf.length() > 11) {
            return "nulo";
        }
        return cpf;
    }

    /**
     * Normaliza defensivamente o telefone para manter consistência com limites de colunas.
     *
     * @param telefone valor original informado
     * @return "nulo" caso exceda 11 caracteres, ou o próprio telefone
     */
    private String normalizarTelefone(String telefone) {
        if (telefone != null && telefone.length() > 11) {
            return "nulo";
        }
        return telefone;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do pecuarista.
     *
     * @return string formatada contendo os atributos do pecuarista
     */
    @Override
    public String toString() {
        return "PecuaristaModel{" +
                "id=" + id +
                ", cpf='" + cpf + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", nome='" + nome + '\'' +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
