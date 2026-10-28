package com.efficientia.efficientia.exception;

/**
 * Exceção lançada quando ocorre tentativa de inserção ou alteração de um CNPJ
 * já existente no banco de dados (violação da constraint UNIQUE da tabela empresa).
 */
public class CnpjDuplicadoException extends DadoDuplicadoException {

    public CnpjDuplicadoException(String entidade, String cnpj, String constraintName, Throwable cause) {
        super(
                entidade != null ? entidade : "Empresa",
                "CNPJ",
                cnpj,
                constraintName,
                "cnpj_duplicado",
                cause
        );
    }

    public CnpjDuplicadoException(String cnpj) {
        this("Empresa", cnpj, null, null);
    }
}
