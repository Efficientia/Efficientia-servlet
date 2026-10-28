package com.efficientia.efficientia.exception;

/**
 * Exceção lançada quando ocorre tentativa de persistência ou atualização de um caminhão
 * com placa (cavalo ou carreta) que já está atribuída a outro veículo no banco de dados.
 */
public class PlacaDuplicadaException extends DadoDuplicadoException {

    public PlacaDuplicadaException(String entidade, String tipoPlaca, String placa, String constraintName, Throwable cause) {
        super(
                entidade != null ? entidade : "Caminhão",
                tipoPlaca != null ? tipoPlaca : "Placa",
                placa,
                constraintName,
                "placa_duplicada",
                cause
        );
    }

    public PlacaDuplicadaException(String placa) {
        this("Caminhão", "Placa", placa, null, null);
    }
}
