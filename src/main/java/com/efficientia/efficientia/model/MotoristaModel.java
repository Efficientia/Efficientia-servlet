package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo representativo de um Motorista no sistema Efficientia.
 *
 * O Motorista é um tipo especializado de {@link UsuarioModel}, responsável
 * por conduzir veículos (caminhões) durante os trajetos de transporte de gado,
 * registrando início, fim, paradas imprevistas e assinando os relatórios de viagem.
 * Todo motorista é obrigatoriamente vinculado a uma {@link EmpresaModel}.
 */
public class MotoristaModel extends UsuarioModel {

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID, vínculo de Empresa e assinatura.
     * Utilizado na leitura e recuperação de registros diretamente do banco de dados.
     *
     * @param id             identificador único do motorista
     * @param empresaModel   empresa/transportadora à qual o motorista está vinculado
     * @param nome           nome completo do motorista
     * @param assinatura     assinatura digitalizada ou identificador de rubrica vindo do banco
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
     * Construtor completo com ID e vínculo com Empresa (sem assinatura).
     * Utilizado na edição/atualização via formulário do site.
     *
     * @param id             identificador único do motorista
     * @param empresaModel   empresa/transportadora à qual o motorista está vinculado
     * @param nome           nome completo do motorista
     * @param dataNascimento data de nascimento do motorista
     * @param senha          senha de autenticação no aplicativo/sistema
     * @param email          e-mail para login e contato
     * @param telefone       número de telefone celular do motorista
     */
    public MotoristaModel(int id,
                          EmpresaModel empresaModel,
                          String nome,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, empresaModel, nome, null, dataNascimento, senha, email, telefone);
    }

    /**
     * Construtor sem ID e com vínculo com Empresa (com assinatura).
     *
     * @param empresaModel   empresa/transportadora à qual o motorista pertence
     * @param nome           nome completo do motorista
     * @param assinatura     assinatura digitalizada ou rubrica
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
     * Construtor sem ID e com vínculo com Empresa (sem assinatura).
     * Utilizado no cadastro de novos motoristas pelo site (onde o site não cadastra assinatura).
     *
     * @param empresaModel   empresa/transportadora à qual o motorista pertence
     * @param nome           nome completo do motorista
     * @param dataNascimento data de nascimento do motorista
     * @param senha          senha de acesso ao sistema
     * @param email          e-mail para login
     * @param telefone       número de telefone celular
     */
    public MotoristaModel(EmpresaModel empresaModel,
                          String nome,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(empresaModel, nome, null, dataNascimento, senha, email, telefone);
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do motorista.
     *
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