package com.efficientia.efficientia.exception;

/**
 * Exceção lançada quando ocorre tentativa de cadastro ou atualização com um e-mail
 * que já se encontra em uso por outro usuário ou entidade no banco de dados.
 */
public class EmailDuplicadoException extends DadoDuplicadoException {

    public EmailDuplicadoException(String entidade, String email, String constraintName, Throwable cause) {
        super(
                entidade != null ? entidade : "Usuário",
                "E-mail",
                email,
                constraintName,
                "email_duplicado",
                cause
        );
    }

    public EmailDuplicadoException(String email) {
        this("Usuário", email, null, null);
    }
}
