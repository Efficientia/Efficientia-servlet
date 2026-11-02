package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class LandingSolutionPageTest {

    private final String solution;

    LandingSolutionPageTest() throws IOException {
        String page = Files.readString(Path.of("src/main/webapp/index.html"));
        int start = page.indexOf("<section id=\"solucao\"");
        assertTrue(start >= 0, "A seção Solução deve existir");
        solution = page.substring(start, page.indexOf("</section>", start));
    }

    @Test
    void mantemTresEtapasDoCarrossel() {
        List<String> cards = elementsWithClass(solution, "div", "card-solucao");
        assertEquals(3, cards.size());
        for (int index = 0; index < 3; index++) {
            assertTrue(cards.get(index).contains("data-index=\"" + index + "\""));
        }
    }

    @Test
    void usaOsTresMockupsAprovadosNaOrdemDasEtapas() {
        List<String> images = elementsWithClass(solution, "div", "imagem-card-solucao");
        assertEquals(3, images.size());
        List<String> expected = List.of(
                "assets/mockup-v3-documentos.png",
                "assets/mockup-v3-chegada.png",
                "assets/mockup-v3-assinatura.png");
        assertAll("Mockups aprovados", java.util.stream.IntStream.range(0, 3)
                .mapToObj(index -> (org.junit.jupiter.api.function.Executable) () ->
                        assertTrue(images.get(index).contains("src=\"" + expected.get(index) + "\""),
                                "Etapa " + (index + 1) + " deve usar " + expected.get(index))));
    }

    @Test
    void mantemApenasDuasSetasExternasAosCards() {
        assertEquals(2, elementsWithClass(solution, "button", "btn-carousel-solucao").size());
        assertEquals(1, elementsWithClass(solution, "button", "btn-prev").size());
        assertEquals(1, elementsWithClass(solution, "button", "btn-next").size());
        for (String card : elementsWithClass(solution, "div", "card-solucao")) {
            assertTrue(elementsWithClass(card, "button", "btn-carousel-solucao").isEmpty());
        }
    }

    @Test
    void mantemUmDotParaCadaEtapa() {
        List<String> dots = elementsWithClass(solution, "button", "dot-solucao");
        assertEquals(3, dots.size());
        for (int index = 0; index < 3; index++) {
            assertTrue(dots.get(index).contains("data-index=\"" + index + "\""));
        }
    }

    @Test
    void naoIncluiControlesOuInstrucoesDirecionaisNosCards() {
        for (String card : elementsWithClass(solution, "div", "card-solucao")) {
            assertFalse(Pattern.compile("<button\\b", Pattern.CASE_INSENSITIVE).matcher(card).find());
            assertFalse(card.contains("data-pan"));
            assertTrue(elementsWithClass(card, "div", "controles-previa").isEmpty());
            assertTrue(elementsWithClass(card, "p", "dica-tela-solucao").isEmpty());
            assertFalse(Pattern.compile("(?iu)deslize|role para|esquerda|direita|acima|abaixo|[←→↑↓↔]")
                    .matcher(card).find(), "O mockup não deve pedir navegação direcional interna");
        }
    }

    private static List<String> elementsWithClass(String html, String tag, String className) {
        var result = new java.util.ArrayList<String>();
        var opening = Pattern.compile("<" + tag + "\\b[^>]*\\bclass=\"([^\"]*)\"[^>]*>").matcher(html);
        var tags = Pattern.compile("</?" + tag + "\\b[^>]*>");
        while (opening.find()) {
            if (!List.of(opening.group(1).split("\\s+")).contains(className)) continue;
            var nested = tags.matcher(html);
            nested.region(opening.end(), html.length());
            int depth = 1;
            int end = -1;
            while (nested.find()) {
                depth += nested.group().startsWith("</") ? -1 : 1;
                if (depth == 0) {
                    end = nested.end();
                    break;
                }
            }
            assertTrue(end >= 0, "Elemento sem fechamento: " + className);
            result.add(html.substring(opening.start(), end));
        }
        return result;
    }
}
