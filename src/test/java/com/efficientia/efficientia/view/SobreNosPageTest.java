package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SobreNosPageTest {
    @Test
    void apresentaHierarquiaEditorialEConteudoAprovado() throws IOException {
        String pagina = Files.readString(Path.of("src/main/webapp/sobre-nos.html"));

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
        assertTrue(pagina.contains("assets/linhas_organicas.svg"));
        assertTrue(pagina.contains("assets/linhas_organicas2.svg"));
        assertTrue(pagina.contains("assets/icone_arquivo_check.svg"));
        assertTrue(pagina.contains("assets/icone_rastreabilidade.svg"));
        assertTrue(pagina.contains("assets/icone_escudo_check.svg"));
    }

    @Test
    void recursosLocaisReferenciadosEstaoDisponiveis() throws IOException {
        Path webapp = Path.of("src/main/webapp");
        String pagina = Files.readString(webapp.resolve("sobre-nos.html"));
        var recursos = Pattern.compile("(?:src|href)=\"((?:assets|css|js)/[^\"]+)\"").matcher(pagina);
        int quantidade = 0;
        while (recursos.find()) {
            String recurso = recursos.group(1).split("\\?", 2)[0];
            Path arquivo = webapp.resolve(recurso);
            assertTrue(Files.isRegularFile(arquivo), "Recurso local ausente: " + recurso);
            assertTrue(Files.size(arquivo) > 0, "Recurso local vazio: " + recurso);
            quantidade++;
        }
        assertTrue(quantidade > 0, "A página deve referenciar seus recursos locais");
    }
}
