package com.efficientia.efficientia.exception;

/**
 * Exceção lançada quando ocorre tentativa de cadastro ou atualização de um trajeto com número de GTA
 * (Guia de Trânsito Animal) ou chave de Nota Fiscal Eletrônica (NF-e) já vinculado a outro transporte no banco.
 */
public class DocumentoFiscalDuplicadoException extends DadoDuplicadoException {

    public DocumentoFiscalDuplicadoException(String tipoDocumento, String numeroDocumento, String constraintName, Throwable cause) {
        super(
                "Trajeto",
                tipoDocumento != null ? tipoDocumento : "Documento Fiscal (GTA/NF-e)",
                numeroDocumento,
                constraintName,
                "documento_fiscal_duplicado",
                cause
        );
    }

    public DocumentoFiscalDuplicadoException(String numeroDocumento) {
        this("Documento Fiscal", numeroDocumento, null, null);
    }
}
