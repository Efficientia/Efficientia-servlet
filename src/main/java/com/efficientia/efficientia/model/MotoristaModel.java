package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo representativo de um Motorista no sistema Efficientia.
 *
 * O Motorista é um tipo especializado de {@link UsuarioModel}, responsável
 * por conduzir veículos (caminhões) durante os trajetos de transporte de gado,
 * registrando início, fim, paradas imprevistas e assinando os relatórios de viagem.
 */
public class MotoristaModel extends UsuarioModel implements Model {

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e vínculo com Empresa.
     * Utilizado na leitura e instanciação de dados vindos do banco de dados.
     *
     * @param id             identificador único do motorista
     * @param empresaModel   empresa/transportadora à qual o motorista está vinculado
     * @param nome           nome completo do motorista
     * @param assinatura     assinatura digitalizada ou identificador de rubrica
     * @param dataNascimento data de nascimento do motorista
     * @param senha          senha de autenticação no aplicativo/sistema
     * @param email          e-mail para login e contato
     * @param telefone       número de telefone celular do motorista
     */
    public MotoristaModel(int id,
                          EmpresaModel empresaModel,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    /**
     * Construtor com ID e sem vínculo com Empresa.
     * Utilizado para carregar motoristas autônomos ou registros sem transportadora definida.
     *
     * @param id             identificador único do motorista
     * @param nome           nome completo do motorista
     * @param assinatura     assinatura digitalizada ou identificador de rubrica
     * @param dataNascimento data de nascimento do motorista
     * @param senha          senha de autenticação no sistema
     * @param email          e-mail para contato
     * @param telefone       número de telefone celular
     */
    public MotoristaModel(int id,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    /**
     * Construtor sem ID e com vínculo com Empresa.
     * Utilizado no cadastro de novos motoristas vinculados a uma transportadora antes da persistência.
     *
     * @param empresaModel   empresa/transportadora à qual o motorista pertence
     * @param nome           nome completo do motorista
     * @param assinatura     assinatura digitalizada ou identificador de rubrica
     * @param dataNascimento data de nascimento do motorista
     * @param senha          senha de autenticação no sistema
     * @param email          e-mail para login
     * @param telefone       número de telefone celular
     */
    public MotoristaModel(EmpresaModel empresaModel,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    /**
     * Construtor sem ID e sem vínculo com Empresa.
     * Utilizado no cadastro de motoristas autônomos antes da persistência.
     *
     * @param nome           nome completo do motorista
     * @param assinatura     assinatura digitalizada ou identificador de rubrica
     * @param dataNascimento data de nascimento do motorista
     * @param senha          senha de autenticação no sistema
     * @param email          e-mail para login
     * @param telefone       número de telefone celular
     */
    public MotoristaModel(String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(nome, assinatura, dataNascimento, senha, email, telefone);
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do motorista.
     * @return string formatada contendo os atributos do motorista
     */
    @Override
    public String toString() {
        return "MotoristaModel{" +
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