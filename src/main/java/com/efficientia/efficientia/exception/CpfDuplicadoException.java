package com.efficientia.efficientia.exception;

/**
 * Exceção lançada quando ocorre tentativa de inserção ou atualização de um CPF
 * que já se encontra previamente registrado na base de dados (violação da constraint UNIQUE).
 */
public class CpfDuplicadoException extends DadoDuplicadoException {

    public CpfDuplicadoException(String entidade, String cpf, String constraintName, Throwable cause) {
        super(
                entidade != null ? entidade : "Pessoa/Usuário",
                "CPF",
                cpf,
                constraintName,
                "cpf_duplicado",
                cause
        );
    }

    public CpfDuplicadoException(String cpf) {
        this("Registro", cpf, null, null);
    }
}
