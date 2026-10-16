package com.efficientia.efficientia.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test void hashValidaSomenteSenhaCorreta() {
        String hash = PasswordHasher.hash("Uma senha longa com ç e espaços");
        assertTrue(PasswordHasher.isHashed(hash));
        assertTrue(PasswordHasher.verify("Uma senha longa com ç e espaços", hash));
        assertFalse(PasswordHasher.verify("errada", hash));
    }
    @Test void saltsSaoIndividuais() {
        assertNotEquals(PasswordHasher.hash("mesma senha"), PasswordHasher.hash("mesma senha"));
    }
    @Test void rejeitaHashMalformado() {
        assertFalse(PasswordHasher.verify("senha", "pbkdf2-sha256$600000$???$???"));
        assertFalse(PasswordHasher.verify("senha", "pbkdf2-sha256$999999999$a$b"));
        assertFalse(PasswordHasher.verify(null, null));
    }
    @Test void permiteValidarLegadoParaMigracao() {
        assertTrue(PasswordHasher.verify("antiga", "antiga"));
        assertFalse(PasswordHasher.verify("outra", "antiga"));
    }
}
