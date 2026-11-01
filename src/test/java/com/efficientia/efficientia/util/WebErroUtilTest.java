package com.efficientia.efficientia.util;

import com.efficientia.efficientia.exception.DadoDuplicadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class WebErroUtilTest {
    @Test
    void duplicidadePreservaMetadadosUtf8ERetornoNoContexto() throws Exception {
        for (String context : new String[]{"", "/Efficientia"}) {
            String[] redirect = new String[1];
            var request = request(context);
            var response = response(redirect);
            var error = new DadoDuplicadoException("Empresa", "E-mail", "ação+teste@example.invalid",
                    "empresa_email_key", "email_duplicado", null);
            WebErroUtil.redirecionarErroDuplicidade(request, response, error, "/empresa?acao=editar&id=2");
            assertTrue(redirect[0].startsWith(context + "/duplicidade.jsp?"));
            Map<String, String> params = Arrays.stream(redirect[0].split("\\?", 2)[1].split("&"))
                    .map(value -> value.split("=", 2))
                    .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));
            assertEquals("email_duplicado", params.get("tipoErro"));
            assertEquals("Empresa", params.get("entidade"));
            assertEquals("E-mail", params.get("campo"));
            assertEquals("ação+teste@example.invalid", params.get("valor"));
            assertEquals(error.getMessage(), params.get("mensagem"));
            assertEquals(context + "/empresa?acao=editar&id=2", params.get("voltar"));
        }
    }

    @Test
    void falhaGeralContinuaNaRotaGeral() throws Exception {
        String[] redirect = new String[1];
        WebErroUtil.redirecionarErroGeral(request("/Efficientia"), response(redirect), null, null);
        assertEquals("/Efficientia/erro.jsp", redirect[0]);
    }

    private HttpServletRequest request(String context) {
        return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class}, (proxy, method, args) ->
                        method.getName().equals("getContextPath") ? context : null);
    }

    private HttpServletResponse response(String[] redirect) {
        return (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{HttpServletResponse.class}, (proxy, method, args) -> {
                    if (method.getName().equals("sendRedirect")) redirect[0] = (String) args[0];
                    return null;
                });
    }
}
