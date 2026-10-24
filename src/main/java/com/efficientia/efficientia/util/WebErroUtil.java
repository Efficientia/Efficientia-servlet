package com.efficientia.efficientia.util;

import com.efficientia.efficientia.exception.DadoDuplicadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utilitário responsável pelo direcionamento padronizado de erros para a
 * tela de erro geral centralizada (/erro.jsp) da aplicação Efficientia.
 */
public final class WebErroUtil {

    private WebErroUtil() { }

    /**
     * Redireciona a requisição para a página de erro geral (/erro.jsp) preenchendo
     * os metadados da exceção de duplicidade e o link de retorno para o formulário.
     *
     * @param req       requisição HTTP atual
     * @param resp      resposta HTTP para redirecionamento
     * @param e         exceção de duplicidade capturada
     * @param urlVoltar caminho relativo de retorno (ex.: "/pecuarista", "/caminhao")
     * @throws IOException em caso de falha de I/O no sendRedirect
     */
    public static void redirecionarErroDuplicidade(
            HttpServletRequest req,
            HttpServletResponse resp,
            DadoDuplicadoException e,
            String urlVoltar
    ) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(req.getContextPath()).append("/erro.jsp?tipoErro=")
          .append(encode(e.getTipoErro() != null ? e.getTipoErro() : "duplicidade"));

        if (e.getEntidade() != null && !e.getEntidade().isBlank()) {
            sb.append("&entidade=").append(encode(e.getEntidade()));
        }
        if (e.getCampo() != null && !e.getCampo().isBlank()) {
            sb.append("&campo=").append(encode(e.getCampo()));
        }
        if (e.getValorDuplicado() != null && !e.getValorDuplicado().isBlank()) {
            sb.append("&valor=").append(encode(e.getValorDuplicado()));
        }
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            sb.append("&mensagem=").append(encode(e.getMessage()));
        }
        if (urlVoltar != null && !urlVoltar.isBlank()) {
            sb.append("&voltar=").append(encode(req.getContextPath() + urlVoltar));
        }

        resp.sendRedirect(sb.toString());
    }

    private static String encode(String valor) {
        if (valor == null) return "";
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}
