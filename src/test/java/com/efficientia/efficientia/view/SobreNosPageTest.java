package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SobreNosPageTest {
    @Test
    void apresentaHierarquiaEditorialEConteudoAprovado() throws IOException {
        String pagina = Files.readString(Path.of("src/main/webapp/sobre-nos.html"));
        String estilos = Files.readString(Path.of("src/main/webapp/css/sobre-nos.css"));

        int historia = pagina.indexOf("<section class=\"sobre-nos-historia\"");
        int principios = pagina.indexOf("<div class=\"sobre-nos-principios\"");
        int equipe = pagina.indexOf("<section class=\"sobre-nos-equipe\"");
        assertTrue(historia >= 0 && historia < principios);
        assertTrue(principios >= 0 && principios < equipe);
        assertTrue(pagina.contains("aria-current=\"page\""));
        assertTrue(pagina.contains("Foto da equipe do Efficientia, formada por alunos do primeiro e do segundo ano."));
        assertTrue(pagina.contains("assets/equipe-efficientia.jpg"));
        assertTrue(pagina.contains("Sobre <span>nós</span>"));
        assertTrue(pagina.contains("<h2 id=\"titulo-historia-sobre-nos\">História</h2>"));
        assertFalse(pagina.contains("class=\"badge"));
        assertTrue(estilos.contains("\"missao visao\" \"valores valores\""));
        assertTrue(estilos.contains(".sobre-nos-equipe{margin-top:22px;padding:22px 24px 24px"));
        assertTrue(pagina.contains("assets/linhas_organicas.svg"));
        assertTrue(pagina.contains("assets/linhas_organicas2.svg"));
        assertTrue(pagina.contains("assets/icone_arquivo_check.svg"));
        assertTrue(pagina.contains("assets/icone_rastreabilidade.svg"));
        assertTrue(pagina.contains("assets/icone_escudo_check.svg"));
        assertTrue(estilos.contains("background:var(--color-white)"));
        assertTrue(estilos.contains(".sobre-nos-card,.sobre-nos-equipe{background:rgb(15 81 174 / 6%)}"));
        assertTrue(estilos.contains("gap:16px"));
        assertTrue(estilos.contains("var(--color-primary-dark)"));
    }
}
