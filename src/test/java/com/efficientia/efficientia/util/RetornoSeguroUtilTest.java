package com.efficientia.efficientia.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RetornoSeguroUtilTest {
    @Test
    void rejeitaDestinosExecutaveisExternosEAmbiguos() {
        for (String contexto : new String[]{"", "/Efficientia"}) {
            for (String destino : new String[]{"javascript:alert(1)", "JaVaScRiPt:alert(1)",
                    "data:text/html,<script>alert(1)</script>", "https://evil.example", "//evil.example",
                    "/Efficientia//evil.example", "/empresa/../erro.jsp", "/empresa%2f..",
                    "/empresa?acao=editar&id=2#javascript:alert(1)", "/empresa\r\nX-Test:1", "\\evil.example", ""}) {
                assertEquals(contexto + "/", RetornoSeguroUtil.resolver(contexto, destino), destino);
            }
            assertEquals(contexto + "/", RetornoSeguroUtil.resolver(contexto, null));
        }
    }

    @Test
    void preservaRotasDeCadastroEEdicaoNosDoisContextos() {
        for (String contexto : new String[]{"", "/Efficientia"}) {
            for (String rota : new String[]{"/admin", "/empresa", "/caminhao", "/motorista", "/pecuarista", "/trajeto"}) {
                assertEquals(contexto + rota, RetornoSeguroUtil.resolver(contexto, rota));
                String edicao = rota + "?acao=editar&id=2";
                assertEquals(contexto + edicao, RetornoSeguroUtil.resolver(contexto, contexto + edicao));
            }
        }
    }
}
