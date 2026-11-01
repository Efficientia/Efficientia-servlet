package com.efficientia.efficientia.dao;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.exception.DadoDuplicadoException;
import com.efficientia.efficientia.factory.ConnectionFactory;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Executar apenas contra o PostgreSQL descartável descrito em docs/poo/PERFIL.md. */
class AdminPerfilPersistenciaTest {
    @BeforeEach void preparar() throws Exception {
        assumeTrue(Boolean.getBoolean("perfil.db.test"), "Requer PostgreSQL descartável e -Dperfil.db.test=true");
        try (Connection c = ConnectionFactory.getConnection()) {
            assertEquals("jdbc:postgresql://127.0.0.1:55439/efficientia_perfil_qa", c.getMetaData().getURL(),
                    "Este teste não deve acessar bancos reais ou compartilhados");
            try (var stmt = c.createStatement()) {
                stmt.executeUpdate("DELETE FROM adm WHERE id IN (9001, 9002)");
                stmt.executeUpdate("INSERT INTO adm(id, id_empresa, nome, email, senha) VALUES "
                        + "(9001, 1, 'Ana teste', 'ana.persistencia@example.invalid', 'hash-preservado'),"
                        + "(9002, 1, 'Bia teste', 'bia.persistencia@example.invalid', 'outro-hash')");
            }
        }
    }

    @AfterEach void limpar() throws Exception {
        if (!Boolean.getBoolean("perfil.db.test")) return;
        try (Connection c = ConnectionFactory.getConnection(); var stmt = c.createStatement()) {
            if ("jdbc:postgresql://127.0.0.1:55439/efficientia_perfil_qa".equals(c.getMetaData().getURL())) {
                stmt.executeUpdate("DELETE FROM adm WHERE id IN (9001, 9002)");
            }
        }
    }

    @Test void persisteAmbosCamposEPreservaAtributosSensiveis() throws Exception {
        AdminDAO dao = new AdminDAO();
        assertTrue(dao.atualizarPerfil(9001, "Ana D'Ávila", "nova.persistencia@example.invalid"));
        var admin = dao.buscar(9001);
        assertEquals("Ana D'Ávila", admin.getNome());
        assertEquals("nova.persistencia@example.invalid", admin.getEmail());
        assertEquals("hash-preservado", admin.getSenha());
        assertEquals(1, admin.getIdEmpresa());
        assertEquals("Bia teste", dao.buscar(9002).getNome());
    }

    @Test void duplicidadeNaoGravaParcialmenteNemMesmoComOutraCaixa() {
        AdminDAO dao = new AdminDAO();
        for (String email : new String[]{"bia.persistencia@example.invalid", "BIA.PERSISTENCIA@example.invalid"}) {
            assertThrows(DadoDuplicadoException.class, () -> dao.atualizarPerfil(9001, "Nome indevido", email));
            assertEquals("Ana teste", dao.buscar(9001).getNome());
            assertEquals("ana.persistencia@example.invalid", dao.buscar(9001).getEmail());
        }
    }

    @Test void permiteManterEmailDaPropriaContaENaoAtualizaIdAusente() throws Exception {
        AdminDAO dao = new AdminDAO();
        assertTrue(dao.atualizarPerfil(9001, "Ana", "ANA.PERSISTENCIA@example.invalid"));
        assertFalse(dao.atualizarPerfil(-1, "Ausente", "ausente@example.invalid"));
    }

    @Test void duasContasNaoAssumemMesmoEmailEmRequisicoesConcorrentes() throws Exception {
        try (var executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch inicio = new CountDownLatch(1);
            Callable<Boolean> primeira = () -> tentarAtualizar(inicio, 9001, "concorrente@example.invalid");
            Callable<Boolean> segunda = () -> tentarAtualizar(inicio, 9002, "CONCORRENTE@example.invalid");
            var a = executor.submit(primeira);
            var b = executor.submit(segunda);
            inicio.countDown();
            assertNotEquals(a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS));
        }
    }

    private boolean tentarAtualizar(CountDownLatch inicio, int id, String email) throws Exception {
        assertTrue(inicio.await(5, TimeUnit.SECONDS));
        try {
            return new AdminDAO().atualizarPerfil(id, "Concorrente", email);
        } catch (DadoDuplicadoException e) {
            return false;
        }
    }
}
