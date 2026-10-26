package com.efficientia.efficientia.exception;

/**
 * Exceção lançada quando ocorre tentativa de cadastro ou atualização com um código de identificação interno
 * (ex: código de integração de empresa ou código corporativo de analista) já existente no banco de dados.
 */
public class CodigoDuplicadoException extends DadoDuplicadoException {

    public CodigoDuplicadoException(String entidade, String codigo, String constraintName, Throwable cause) {
        super(
                entidade != null ? entidade : "Registro",
                "Código",
                codigo,
                constraintName,
                "codigo_duplicado",
                cause
        );
    }

    public CodigoDuplicadoException(String codigo) {
        this("Registro", codigo, null, null);
    }
}
