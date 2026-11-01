package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.filter.AuthFilter;
import com.efficientia.efficientia.model.AdminModel;
import com.efficientia.efficientia.model.EmpresaModel;
import com.efficientia.efficientia.util.TratadorExcecaoBanco;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PerfilServletTest {
    static class Banco extends AdminDAO {
        AdminModel registro = new AdminModel(7, new EmpresaModel(2, "Transportadora teste", "", ""),
                "ana@example.invalid", "hash-que-nao-pode-ser-exposto", "Ana");
        int gravacoes;
        boolean duplicado, indisponivel, removido;

        @Override public AdminModel buscar(int id) { return id == 7 && !removido ? registro : null; }
        @Override public boolean atualizarPerfil(int id, String nome, String email) throws SQLException {
            if (indisponivel) throw new SQLException("detalhe interno do banco");
            if (duplicado) throw TratadorExcecaoBanco.traduzir(
                    new SQLException("duplicate key adm_email_key", "23505"), "Administrador", "email", email);
            if (id != 7 || removido) return false;
            registro = new AdminModel(7, registro.getEmpresa(), email, registro.getSenha(), nome);
            gravacoes++;
            return true;
        }
    }

    static class Requisicao {
        Map<String, Object> atributos = new HashMap<>(), sessao = new HashMap<>();
        Map<String, String> parametros = new HashMap<>();
        String destino, redirecionamento;
        int status = 200;
        boolean autenticado = true, continuou;
        HttpSession session = proxy(HttpSession.class, (m, a) -> switch (m) {
            case "getAttribute" -> sessao.get(a[0]);
            case "setAttribute" -> { sessao.put((String) a[0], a[1]); yield null; }
            case "removeAttribute" -> { sessao.remove(a[0]); yield null; }
            default -> null;
        });
        HttpServletRequest request = proxy(HttpServletRequest.class, (m, a) -> switch (m) {
            case "getParameter" -> parametros.get(a[0]);
            case "getAttribute" -> atributos.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getContextPath" -> "/Efficientia";
            case "getSession" -> autenticado ? session : null;
            case "getRequestDispatcher" -> {
                destino = (String) a[0];
                yield proxy(RequestDispatcher.class, (n, b) -> null);
            }
            default -> null;
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (m, a) -> {
            if (m.equals("sendRedirect")) redirecionamento = (String) a[0];
            if (m.equals("setStatus")) status = (int) a[0];
            return null;
        });
        FilterChain chain = proxy(FilterChain.class, (m, a) -> { continuou = true; return null; });
    }

    interface Chamada { Object executar(String metodo, Object[] argumentos); }
    @SuppressWarnings("unchecked")
    static <T> T proxy(Class<T> tipo, Chamada chamada) {
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo},
                (o, m, a) -> chamada.executar(m.getName(), a));
    }

    Banco banco = new Banco();
    PerfilServlet servlet = new PerfilServlet();
    Requisicao r = new Requisicao();

    PerfilServletTest() throws Exception {
        var campo = PerfilServlet.class.getDeclaredField("adminDAO");
        campo.setAccessible(true);
        campo.set(servlet, banco);
        r.sessao.put("adminLogado", new AdminModel(7, null, "antigo@example.invalid", null, "Antigo"));
    }

    void abrirEPreencher() throws Exception {
        servlet.doGet(r.request, r.response);
        r.parametros.put("csrfToken", (String) r.atributos.get("csrfToken"));
        r.parametros.put("nome", "  Ana Maria  ");
        r.parametros.put("email", "  ana.maria@example.invalid  ");
        r.destino = null;
    }

    @Test void carregaDadosAtuaisEEmpresaSemExporSenha() throws Exception {
        servlet.doGet(r.request, r.response);
        assertEquals("/WEB-INF/views/perfil.jsp", r.destino);
        assertEquals("Ana", r.atributos.get("nomePerfil"));
        assertEquals("ana@example.invalid", r.atributos.get("emailPerfil"));
        assertEquals("Transportadora teste", r.atributos.get("empresaPerfil"));
        assertEquals("A", r.atributos.get("inicialPerfil"));
        assertNotNull(r.atributos.get("csrfToken"));
        assertTrue(r.atributos.values().stream().noneMatch(AdminModel.class::isInstance));
        assertNull(((AdminModel) r.sessao.get("adminLogado")).getSenha());
    }

    @Test void apresentaAusenciaDeEmpresaSemInventarVinculo() throws Exception {
        banco.registro.setEmpresaModel(null);
        servlet.doGet(r.request, r.response);
        assertEquals("Nenhuma empresa vinculada", r.atributos.get("empresaPerfil"));
    }

    @Test void salvaSomenteContaDaSessaoESincronizaSemAlterarSenhaOuEmpresa() throws Exception {
        abrirEPreencher();
        r.parametros.put("id", "99");
        r.parametros.put("idEmpresa", "99");
        r.parametros.put("senha", "senha invasora");
        servlet.doPost(r.request, r.response);
        assertEquals(1, banco.gravacoes);
        assertEquals("Ana Maria", banco.registro.getNome());
        assertEquals("ana.maria@example.invalid", banco.registro.getEmail());
        assertEquals("hash-que-nao-pode-ser-exposto", banco.registro.getSenha());
        assertEquals(2, banco.registro.getIdEmpresa());
        AdminModel sessao = (AdminModel) r.sessao.get("adminLogado");
        assertEquals("Ana Maria", sessao.getNome());
        assertEquals(banco.registro.getEmail(), sessao.getEmail());
        assertNull(sessao.getSenha());
        assertEquals("/Efficientia/perfil", r.redirecionamento);
        servlet.doGet(r.request, r.response);
        assertNotNull(r.atributos.get("sucessoPerfil"));
        r.atributos.clear();
        servlet.doGet(r.request, r.response);
        assertNull(r.atributos.get("sucessoPerfil"));
    }

    @Test void rejeitaCamposInvalidosPreservandoPreenchimento() throws Exception {
        for (String[] valores : new String[][]{{"", "a@example.invalid"}, {"Ana", "invalido"},
                {"A".repeat(151), "a@example.invalid"}, {"Ana", "a".repeat(243) + "@example.com"},
                {"Ana\nMaria", "a@example.invalid"}, {"Ana", ""}}) {
            abrirEPreencher();
            r.parametros.put("nome", valores[0]);
            r.parametros.put("email", valores[1]);
            servlet.doPost(r.request, r.response);
            assertEquals(422, r.status);
            assertEquals("/WEB-INF/views/perfil.jsp", r.destino);
            assertEquals(valores[0], r.atributos.get("nomePerfil"));
            assertEquals(valores[1], r.atributos.get("emailPerfil"));
            assertNotNull(r.atributos.get("erroPerfil"));
            assertEquals(0, banco.gravacoes);
        }
    }

    @Test void aceitaLimitesDoDominio() throws Exception {
        abrirEPreencher();
        r.parametros.put("nome", "A".repeat(150));
        r.parametros.put("email", "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(61));
        servlet.doPost(r.request, r.response);
        assertEquals(1, banco.gravacoes);
    }

    @Test void emailDuplicadoNaoAlteraDadosNemSessao() throws Exception {
        abrirEPreencher();
        banco.duplicado = true;
        servlet.doPost(r.request, r.response);
        assertEquals(409, r.status);
        assertNotNull(r.atributos.get("erroEmail"));
        assertEquals("Ana", ((AdminModel) r.sessao.get("adminLogado")).getNome());
        assertEquals("Ana Maria", r.atributos.get("nomePerfil"));
        assertEquals(0, banco.gravacoes);
    }

    @Test void falhaDoBancoNaoExibeDetalhesNemConfirmaSucesso() throws Exception {
        abrirEPreencher();
        banco.indisponivel = true;
        servlet.doPost(r.request, r.response);
        assertEquals(503, r.status);
        assertNull(r.redirecionamento);
        assertFalse(r.atributos.get("erroPerfil").toString().contains("detalhe interno"));
        assertEquals("Ana", ((AdminModel) r.sessao.get("adminLogado")).getNome());
    }

    @Test void contaAusenteNaoPermiteGravacao() throws Exception {
        abrirEPreencher();
        banco.removido = true;
        servlet.doPost(r.request, r.response);
        assertEquals(503, r.status);
        assertEquals(0, banco.gravacoes);
    }

    @Test void csrfAusenteOuInvalidoBloqueiaGravacao() throws Exception {
        abrirEPreencher();
        for (String token : new String[]{null, "forjado"}) {
            r.parametros.put("csrfToken", token);
            servlet.doPost(r.request, r.response);
            assertEquals(403, r.status);
            assertEquals(0, banco.gravacoes);
        }
    }

    @Test void repeticaoDoMesmoPostNaoGravaDuasVezes() throws Exception {
        abrirEPreencher();
        servlet.doPost(r.request, r.response);
        servlet.doPost(r.request, r.response);
        assertEquals(403, r.status);
        assertEquals(1, banco.gravacoes);
    }

    @Test void tokenDeOutraSessaoNaoEhAceito() throws Exception {
        abrirEPreencher();
        var outra = new Requisicao();
        outra.sessao.put("adminLogado", banco.registro);
        servlet.doGet(outra.request, outra.response);
        assertNotEquals(r.atributos.get("csrfToken"), outra.atributos.get("csrfToken"));
        r.parametros.put("csrfToken", (String) outra.atributos.get("csrfToken"));
        servlet.doPost(r.request, r.response);
        assertEquals(403, r.status);
        assertEquals(0, banco.gravacoes);
    }

    @Test void protegeGetEPostSemSessao() throws Exception {
        r.autenticado = false;
        servlet.doGet(r.request, r.response);
        assertEquals("/Efficientia/login?erro=nao_autorizado", r.redirecionamento);
        servlet.doPost(r.request, r.response);
        assertEquals("/Efficientia/login?erro=nao_autorizado", r.redirecionamento);
        assertEquals(0, banco.gravacoes);
    }

    @Test void filtroProtegeRotaPerfilESegueApenasComAutenticacao() throws Exception {
        assertTrue(Arrays.asList(AuthFilter.class.getAnnotation(WebFilter.class).urlPatterns()).contains("/perfil"));
        var filtro = new AuthFilter();
        r.autenticado = false;
        filtro.doFilter(r.request, r.response, r.chain);
        assertFalse(r.continuou);
        assertEquals("/Efficientia/login?erro=nao_autorizado", r.redirecionamento);
        r.autenticado = true;
        filtro.doFilter(r.request, r.response, r.chain);
        assertTrue(r.continuou);
    }
}
