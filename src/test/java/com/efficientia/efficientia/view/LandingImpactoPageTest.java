package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class LandingImpactoPageTest {
    private static final Path WEBAPP = Path.of("src/main/webapp");

    @Test
    void utilizaAComposicaoAprovadaSemSobreporOutrosCardsOuNotebooks() throws Exception {
        String pagina = Files.readString(WEBAPP.resolve("index.html"));
        int inicio = pagina.indexOf("id=\"impacto\"");
        assertTrue(inicio >= 0);
        String impacto = pagina.substring(inicio, pagina.indexOf("</section>", inicio));

        assertTrue(impacto.contains("src=\"assets/mockup_macbook_impacto.png?v=2\""));
        assertFalse(impacto.contains("mockup_pc_impacto.svg"));
        assertFalse(impacto.contains("mockup_pc_impacto-mobile.svg"));
        assertTrue(impacto.contains("alt=\"MacBook"));
        assertFalse(impacto.contains("impacto-decoracao"));
        assertFalse(impacto.contains("impacto-mobile-chamadas"));
        assertEquals(1, impacto.split("<img\\b", -1).length - 1);
        assertTrue(impacto.contains("width=\"1397\" height=\"744\""));
        for (String beneficio : new String[]{"dados organizados", "dashboard", "menos retrabalho",
                "cadastros centralizados", "registros seguros"}) {
            assertTrue(impacto.contains(beneficio));
        }
    }

    @Test
    void mantemResolucaoECanalAlphaDoPngAprovado() throws Exception {
        Path arquivo = WEBAPP.resolve("assets/mockup_macbook_impacto.png");
        assertTrue(Files.isRegularFile(arquivo));
        var imagem = ImageIO.read(arquivo.toFile());
        assertNotNull(imagem);
        assertEquals(1397, imagem.getWidth());
        assertEquals(744, imagem.getHeight());
        assertTrue(imagem.getColorModel().hasAlpha());
        assertEquals(0, imagem.getRGB(0, 0) >>> 24);
        assertTrue((imagem.getRGB(698, 372) >>> 24) > 128);
    }

    @Test
    void preservaOsBytesDoArquivoFinalAprovado() throws Exception {
        byte[] conteudo = Files.readAllBytes(WEBAPP.resolve("assets/mockup_macbook_impacto.png"));
        String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(conteudo));
        assertEquals("167dffda7b6145dc6aac7a373d14419e611919fd8c072fe02fc149ad40b8a555", hash);
    }
}
