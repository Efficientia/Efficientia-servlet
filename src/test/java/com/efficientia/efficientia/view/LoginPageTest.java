package com.efficientia.efficientia.view;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTest {

    @Test
    void mantemBotaoEntrarSemExibirTituloDuplicado() throws IOException {
        String pagina = Files.readString(Path.of("src/main/webapp/login.jsp"));

        assertFalse(pagina.contains("<h1 class=\"titulo-login\">Entrar</h1>"));
        assertTrue(pagina.contains("<button type=\"submit\">Entrar</button>"));
    }
}
