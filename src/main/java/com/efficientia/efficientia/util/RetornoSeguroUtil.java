package com.efficientia.efficientia.util;

import java.util.regex.Pattern;

/** Limita o retorno dos avisos às telas de cadastro da aplicação. */
public final class RetornoSeguroUtil {
    private static final Pattern ROTA = Pattern.compile(
            "/(?:admin|empresa|caminhao|motorista|pecuarista|trajeto)(?:\\?acao=editar&id=[1-9][0-9]*)?");

    private RetornoSeguroUtil() { }

    public static String resolver(String contexto, String destino) {
        String inicio = contexto + "/";
        if (destino == null) return inicio;
        String rota = destino;
        if (!contexto.isEmpty() && destino.startsWith(contexto + "/")) {
            rota = destino.substring(contexto.length());
        }
        return ROTA.matcher(rota).matches() ? contexto + rota : inicio;
    }
}
