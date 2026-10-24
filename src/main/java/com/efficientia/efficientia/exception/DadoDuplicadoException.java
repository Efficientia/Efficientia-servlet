package com.efficientia.efficientia.exception;

/**
 * Exceção base para tratamento de duplicidade de dados e violação de restrições de unicidade (UNIQUE constraints)
 * na camada de persistência com o banco de dados relacional (PostgreSQL).
 *
 * <p>Estende {@link RuntimeException} para permitir tratamento resiliente nas camadas de controle (Servlets)
 * e regras de negócio, sem quebrar a assinatura dos métodos existentes do DAO.</p>
 */
public class DadoDuplicadoException extends RuntimeException {

    private final String entidade;
    private final String campo;
    private final String valorDuplicado;
    private final String constraintName;
    private final String tipoErro;

    /**
     * Construtor básico com mensagem descritiva.
     *
     * @param mensagem mensagem de erro
     */
    public DadoDuplicadoException(String mensagem) {
        super(mensagem);
        this.entidade = "Entidade";
        this.campo = "Campo Único";
        this.valorDuplicado = null;
        this.constraintName = null;
        this.tipoErro = "dado_duplicado";
    }

    /**
     * Construtor com mensagem e causa original (SQLException).
     *
     * @param mensagem mensagem de erro
     * @param cause    causa original da exceção
     */
    public DadoDuplicadoException(String mensagem, Throwable cause) {
        super(mensagem, cause);
        this.entidade = "Entidade";
        this.campo = "Campo Único";
        this.valorDuplicado = null;
        this.constraintName = null;
        this.tipoErro = "dado_duplicado";
    }

    /**
     * Construtor detalhado contendo metadados completos da violação no banco de dados.
     *
     * @param entidade       nome da entidade afetada (ex: "Pecuarista", "Empresa", "Motorista")
     * @param campo          nome do campo violado (ex: "CPF", "CNPJ", "E-mail")
     * @param valorDuplicado valor cujo cadastro foi rejeitado por já existir no banco
     * @param constraintName nome da constraint UNIQUE no banco (ex: "pecuarista_cpf_key")
     * @param tipoErro       identificador textual padronizado para feedback em URLs e Views
     * @param cause          exceção SQL original
     */
    public DadoDuplicadoException(
            String entidade,
            String campo,
            String valorDuplicado,
            String constraintName,
            String tipoErro,
            Throwable cause
    ) {
        super(montarMensagem(entidade, campo, valorDuplicado, constraintName), cause);
        this.entidade = entidade != null ? entidade : "Entidade";
        this.campo = campo != null ? campo : "Campo Único";
        this.valorDuplicado = valorDuplicado;
        this.constraintName = constraintName;
        this.tipoErro = tipoErro != null ? tipoErro : "dado_duplicado";
    }

    private static String montarMensagem(String entidade, String campo, String valorDuplicado, String constraintName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Violação de unicidade no banco de dados: ");
        if (entidade != null) {
            sb.append(entidade).append(" com ");
        }
        sb.append(campo != null ? campo : "campo único");
        if (valorDuplicado != null && !valorDuplicado.isBlank()) {
            sb.append(" '").append(valorDuplicado).append("'");
        }
        sb.append(" já cadastrado no sistema.");
        if (constraintName != null && !constraintName.isBlank()) {
            sb.append(" (Constraint: ").append(constraintName).append(")");
        }
        return sb.toString();
    }

    public String getEntidade() {
        return entidade;
    }

    public String getCampo() {
        return campo;
    }

    public String getValorDuplicado() {
        return valorDuplicado;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getTipoErro() {
        return tipoErro;
    }
}
