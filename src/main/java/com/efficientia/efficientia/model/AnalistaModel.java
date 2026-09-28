package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo representativo do Analista no sistema Efficientia.
 *
 * O Analista é um tipo especializado de {@link UsuarioModel}, encarregado de
 * auditar trajetos, validar conformidades de transporte e bem-estar animal,
 * emitir pareceres e aprovar ou reprovar relatórios de viagem.
 */
public class AnalistaModel extends UsuarioModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Cadastro de Pessoa Física (CPF) do analista. */
    private String cpf;

    /** Código de registro profissional ou identificador funcional interno. */
    private String codigo;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e vínculo com Empresa.
     * Utilizado na reconstituição do analista a partir de consultas ao banco de dados.
     *
     * @param id             identificador único do analista
     * @param empresaModel   empresa à qual o analista está associado
     * @param cpf            número do CPF (máximo de 11 caracteres numéricos)
     * @param nome           nome completo do analista
     * @param assinatura     assinatura digitalizada ou rubrica
     * @param dataNascimento data de nascimento
     * @param senha          senha de acesso ao sistema
     * @param email          e-mail institucional ou para contato
     * @param telefone       número de telefone para contato
     * @param codigo         código de registro profissional / funcional
     */
    public AnalistaModel(int id,
                         EmpresaModel empresaModel,
                         String cpf,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(id, empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
        this.cpf = cpf;
        this.codigo = codigo;

        // Normalização defensiva: anula CPF caso exceda o limite de 11 dígitos
        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }
    }

    /**
     * Construtor com ID e sem vínculo com Empresa.
     * Utilizado para analistas independentes ou registros sem empresa associada.
     *
     * @param id             identificador único do analista
     * @param cpf            número do CPF (máximo de 11 caracteres numéricos)
     * @param nome           nome completo do analista
     * @param assinatura     assinatura digitalizada ou rubrica
     * @param dataNascimento data de nascimento
     * @param senha          senha de acesso ao sistema
     * @param email          e-mail institucional ou para contato
     * @param telefone       número de telefone para contato
     * @param codigo         código de registro funcional
     */
    public AnalistaModel(int id,
                         String cpf,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
        this.cpf = cpf;
        this.codigo = codigo;

        // Normalização defensiva: anula CPF caso exceda o limite de 11 dígitos
        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }
    }

    /**
     * Construtor sem ID e com vínculo com Empresa.
     * Utilizado no cadastro de um novo analista vinculado a uma empresa antes da persistência.
     *
     * @param empresaModel   empresa à qual o analista será vinculado
     * @param cpf            número do CPF (máximo de 11 caracteres numéricos)
     * @param nome           nome completo do analista
     * @param assinatura     assinatura digitalizada ou rubrica
     * @param dataNascimento data de nascimento
     * @param senha          senha de acesso ao sistema
     * @param email          e-mail para login
     * @param telefone       número de telefone
     * @param codigo         código de registro funcional
     */
    public AnalistaModel(EmpresaModel empresaModel,
                         String cpf,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
        this.cpf = cpf;
        this.codigo = codigo;

        // Normalização defensiva: anula CPF caso exceda o limite de 11 dígitos
        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }
    }

    /**
     * Construtor sem ID e sem vínculo com Empresa.
     * Utilizado no cadastro inicial de um analista avulso.
     *
     * @param nome           nome completo do analista
     * @param assinatura     assinatura digitalizada ou rubrica
     * @param dataNascimento data de nascimento
     * @param senha          senha de acesso ao sistema
     * @param email          e-mail para login
     * @param telefone       número de telefone
     * @param codigo         código de registro funcional
     */
    public AnalistaModel(String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(nome, assinatura, dataNascimento, senha, email, telefone);
        this.codigo = codigo;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o CPF do analista.
     * @return CPF formatado ou numérico
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do analista.
     * @param cpf novo número de CPF
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Obtém o código funcional do analista.
     * @return código de registro profissional
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Define o código funcional do analista.
     * @param codigo novo código de registro
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do analista.
     * @return string formatada contendo os atributos do analista
     */
    @Override
    public String toString() {
        return "AnalistaModel{" +
                "id=" + id +
                ", empresaModel=" + empresaModel +
                ", cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", codigo='" + codigo + '\'' +
                '}';
    }
}
