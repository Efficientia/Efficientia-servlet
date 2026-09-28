package com.efficientia.efficientia.model;

/**
 * Enumeração dos estados possíveis para a Análise de um Trajeto no sistema Efficientia.
 *
 * Representa as etapas do fluxo de auditoria e validação técnica realizada
 * pelo analista responsável.
 */
public enum StatusAnalise {

    /** A análise foi totalmente concluída e o parecer técnico emitido. */
    CONCLUIDA,

    /** A análise técnica encontra-se em progresso ou sob revisão pelo analista. */
    EM_ANDAMENTO
}
