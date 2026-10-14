package com.efficientia.efficientia.model;

/**
 * Enumeração dos estados operacionais de um Trajeto / Viagem no sistema Efficientia.
 *
 * Controla o ciclo de vida do transporte pecuário desde a partida da fazenda
 * até o desembarque definitivo no destino.
 */
public enum StatusTrajeto {

    /** O trajeto foi finalizado e os animais foram descarregados no destino. */
    CONCLUIDA,

    /** O transporte está em andamento (caminhão em deslocamento na rota). */
    EM_ANDAMENTO;

    /**
     * Converte defensivamente um texto em {@link StatusTrajeto}.
     * Trata maiúsculas/minúsculas, espaços extras e variações com acentuação.
     * Retorna {@link #EM_ANDAMENTO} como padrão em caso de valor nulo, vazio ou inválido.
     *
     * @param valor texto representando o status
     * @return {@link StatusTrajeto} correspondente ou {@link #EM_ANDAMENTO}
     */
    public static StatusTrajeto from(String valor) {
        return from(valor, EM_ANDAMENTO);
    }

    /**
     * Converte defensivamente um texto em {@link StatusTrajeto} com fallback customizado.
     *
     * @param valor texto representando o status
     * @param padrao valor de retorno padrão em caso de falha
     * @return {@link StatusTrajeto} correspondente ou o padrão fornecido
     */
    public static StatusTrajeto from(String valor, StatusTrajeto padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        String limpo = java.text.Normalizer.normalize(valor, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase()
                .replace(" ", "_");
        try {
            return StatusTrajeto.valueOf(limpo);
        } catch (IllegalArgumentException e) {
            return padrao;
        }
    }
}
