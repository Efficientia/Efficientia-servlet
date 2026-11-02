package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LandingImpactoPageTest {
    private static final Path WEBAPP = Path.of("src/main/webapp");

    @Test
    void utilizaOMacBookAprovadoSemAlternarParaOsNotebooksAntigos() throws Exception {
        String pagina = Files.readString(WEBAPP.resolve("index.html"));
        int inicio = pagina.indexOf("id=\"impacto\"");
        assertTrue(inicio >= 0);
        String impacto = pagina.substring(inicio, pagina.indexOf("</section>", inicio));

        assertTrue(impacto.contains("src=\"assets/mockup_macbook_impacto.png\""));
        assertFalse(impacto.contains("mockup_pc_impacto.svg"));
        assertFalse(impacto.contains("mockup_pc_impacto-mobile.svg"));
        assertTrue(impacto.contains("alt=\"MacBook"));
        assertTrue(impacto.contains("width=\"1720\" height=\"914\""));
    }

    @Test
    void mantemResolucaoECanalAlphaDoPngAprovado() throws Exception {
        Path arquivo = WEBAPP.resolve("assets/mockup_macbook_impacto.png");
        assertTrue(Files.isRegularFile(arquivo));
        var imagem = ImageIO.read(arquivo.toFile());
        assertNotNull(imagem);
        assertEquals(1720, imagem.getWidth());
        assertEquals(914, imagem.getHeight());
        assertTrue(imagem.getColorModel().hasAlpha());
        assertEquals(0, imagem.getRGB(0, 0) >>> 24);
        assertTrue((imagem.getRGB(860, 457) >>> 24) > 128);
    }

    @Test
    void preservaChamadasSeparadasSemIncluirOutroNotebookNaDecoracao() throws Exception {
        Path arquivo = WEBAPP.resolve("assets/impacto-chamadas.svg");
        assertTrue(Files.isRegularFile(arquivo));
        String decoracao = Files.readString(arquivo);
        assertTrue(decoracao.contains("<path"));
        assertFalse(decoracao.contains("<image"));
        assertFalse(decoracao.contains("data:image"));
        assertFalse(decoracao.contains("<filter"));

        String pagina = Files.readString(WEBAPP.resolve("index.html"));
        for (String beneficio : new String[]{"Dados organizados", "Dashboard", "Registros seguros", "Menos retrabalho"}) {
            assertTrue(pagina.contains("<li>" + beneficio + "</li>"));
        }
    }
}
