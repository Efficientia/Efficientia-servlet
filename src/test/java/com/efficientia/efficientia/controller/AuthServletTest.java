package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.model.AdminModel;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/** Exercita o fluxo dos servlets sem criar contas no banco real. */
class AuthServletTest {
    static class Exchange {
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> sessionAttributes = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        String forwarded, redirected;
        boolean rotated, invalidated;
        int timeout;
        HttpSession session = proxy(HttpSession.class, (method, args) -> switch (method) {
            case "getAttribute" -> sessionAttributes.get(args[0]);
            case "setAttribute" -> { sessionAttributes.put((String) args[0], args[1]); yield null; }
            case "setMaxInactiveInterval" -> { timeout = (int) args[0]; yield null; }
            case "invalidate" -> { invalidated = true; yield null; }
            default -> null;
        });
        HttpServletRequest request = proxy(HttpServletRequest.class, (method, args) -> switch (method) {
            case "getParameter" -> params.get(args[0]);
            case "setAttribute" -> { attributes.put((String) args[0], args[1]); yield null; }
            case "getContextPath" -> "/Efficientia";
            case "getServletPath" -> "/logout";
            case "getSession" -> session;
            case "changeSessionId" -> { rotated = true; yield "new-session"; }
            case "getRequestDispatcher" -> {
                forwarded = (String) args[0];
                yield proxy(RequestDispatcher.class, (m, a) -> null);
            }
            default -> null;
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (method, args) -> {
            if (method.equals("sendRedirect")) redirected = (String) args[0];
            return null;
        });
    }
    interface Call { Object invoke(String method, Object[] args); }
    @SuppressWarnings("unchecked")
    static <T> T proxy(Class<T> type, Call call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (object, method, args) -> call.invoke(method.getName(), args));
    }
    static void inject(Object servlet, AdminDAO dao) throws Exception {
        var field = servlet.getClass().getDeclaredField("adminDAO");
        field.setAccessible(true);
        field.set(servlet, dao);
    }
    @Test void loginRenovaSessaoERedirecionaComContexto() throws Exception {
        var servlet = new LoginServlet();
        var admin = new AdminModel(1, null, "teste@example.invalid", null, "Teste");
        inject(servlet, new AdminDAO() {
            @Override public AdminModel autenticar(String email, String senha) { return admin; }
        });
        var e = new Exchange();
        e.params.put("email", "teste@example.invalid");
        e.params.put("senha", "senha de teste");
        servlet.doPost(e.request, e.response);
        assertTrue(e.rotated);
        assertEquals(1800, e.timeout);
        assertSame(admin, e.sessionAttributes.get("adminLogado"));
        assertEquals("/Efficientia/teste.jsp", e.redirected);
    }
    @Test void loginInvalidoMantemEmailSemCriarAutenticacao() throws Exception {
        var servlet = new LoginServlet();
        inject(servlet, new AdminDAO() {
            @Override public AdminModel autenticar(String email, String senha) { return null; }
        });
        var e = new Exchange();
        e.params.put("email", "teste@example.invalid");
        e.params.put("senha", "errada");
        servlet.doPost(e.request, e.response);
        assertEquals("/login.jsp", e.forwarded);
        assertEquals("teste@example.invalid", e.attributes.get("emailInformado"));
        assertTrue(e.sessionAttributes.isEmpty());
    }
    @Test void cadastroRejeitaSenhaCurtaAntesDoBanco() throws Exception {
        var e = new Exchange();
        e.params.put("nome", "Teste");
        e.params.put("email", "teste@example.invalid");
        e.params.put("senha", "curta");
        e.params.put("nomeEmpresa", "Empresa de teste");
        e.params.put("cnpj", "12345678000190");
        new CadastroServlet().doPost(e.request, e.response);
        assertEquals("/cadastro.jsp", e.forwarded);
        assertNotNull(e.attributes.get("erro"));
        assertFalse(e.attributes.containsKey("senha"));
    }
    @Test void logoutEncerraSessao() throws Exception {
        var e = new Exchange();
        new LoginServlet().doGet(e.request, e.response);
        assertTrue(e.invalidated);
        assertEquals("/Efficientia/login?msg=desconectado", e.redirected);
    }

    @Test void cadastroCriaEmpresaEAdministradorComDadosNormalizados() throws Exception {
        var servlet = new CadastroServlet();
        inject(servlet, new AdminDAO() {
            @Override public boolean emailCadastrado(String email) { return false; }
            @Override public void cadastrarComEmpresa(String empresa, String cnpj, String nome,
                                                      String email, String senha) {
                assertEquals("Empresa de teste", empresa);
                assertEquals("12345678000190", cnpj);
                assertEquals("Teste", nome);
                assertEquals("teste@example.invalid", email);
            }
        });
        var e = cadastroValido();
        servlet.doPost(e.request, e.response);
        assertEquals("/Efficientia/login?msg=cadastrado", e.redirected);
    }

    @Test void cadastroDuplicadoNaoCriaEmpresa() throws Exception {
        var servlet = new CadastroServlet();
        inject(servlet, new AdminDAO() {
            @Override public boolean emailCadastrado(String email) { return true; }
            @Override public void cadastrarComEmpresa(String a, String b, String c, String d, String f) {
                fail("Não deve criar empresa quando o e-mail já está cadastrado.");
            }
        });
        var e = cadastroValido();
        servlet.doPost(e.request, e.response);
        assertEquals("/cadastro.jsp", e.forwarded);
        assertNotNull(e.attributes.get("erro"));
        assertEquals(" Empresa de teste ", e.attributes.get("empresaInformada"));
    }

    @Test void cadastroRejeitaCnpjInvalidoAntesDoBanco() throws Exception {
        var e = cadastroValido();
        e.params.put("cnpj", "123");
        new CadastroServlet().doPost(e.request, e.response);
        assertEquals("/cadastro.jsp", e.forwarded);
        assertNotNull(e.attributes.get("erro"));
    }

    static Exchange cadastroValido() {
        var e = new Exchange();
        e.params.put("nome", " Teste ");
        e.params.put("nomeEmpresa", " Empresa de teste ");
        e.params.put("cnpj", "12.345.678/0001-90");
        e.params.put("email", "TESTE@example.invalid");
        e.params.put("senha", "Uma senha de teste longa");
        return e;
    }
}
