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
    EM_ANDAMENTO
}
