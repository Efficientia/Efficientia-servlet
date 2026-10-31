package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorPageTest {
    @Test
    void mapsNotFoundAndServerErrorsToTheErrorScreen() throws Exception {
        Path webXml = Path.of("src/main/webapp/WEB-INF/web.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(webXml.toFile());
        NodeList pages = document.getElementsByTagNameNS("https://jakarta.ee/xml/ns/jakartaee", "error-page");
        Set<String> codes = new HashSet<>();
        Set<String> locations = new HashSet<>();

        for (int index = 0; index < pages.getLength(); index++) {
            Element page = (Element) pages.item(index);
            NodeList pageCodes = page.getElementsByTagNameNS("https://jakarta.ee/xml/ns/jakartaee", "error-code");
            NodeList pageLocations = page.getElementsByTagNameNS("https://jakarta.ee/xml/ns/jakartaee", "location");
            if (pageCodes.getLength() > 0) {
                String code = pageCodes.item(0).getTextContent().trim();
                codes.add(code);
                if (Set.of("404", "500").contains(code)) {
                    assertEquals("/erro.jsp", pageLocations.item(0).getTextContent().trim());
                }
            }
            if (pageLocations.getLength() > 0) {
                locations.add(pageLocations.item(0).getTextContent().trim());
            }
        }

        assertTrue(codes.containsAll(Set.of("404", "500")));
        assertTrue(locations.contains("/erro.jsp"));
    }

    @Test
    void errorScreenUsesTheApprovedCopyAndLocalFigmaDecorations() throws Exception {
        String page = Files.readString(Path.of("src/main/webapp/erro.jsp"));

        assertTrue(page.contains("Ops, algo deu errado!"));
        assertTrue(page.contains("tente novamente mais tarde"));
        assertTrue(page.contains("/css/erro.css"));
        assertEquals(1, page.split("error-decoration--bottom-small", -1).length - 1);
        for (String asset : Set.of(
                "error-glow-top-large.svg",
                "error-glow-bottom-large.svg",
                "error-glow-ellipse-159.svg",
                "error-glow-ellipse-161.svg")) {
            assertTrue(page.contains(asset));
            assertTrue(Files.size(Path.of("src/main/webapp/assets", asset)) > 0);
        }
    }
}
