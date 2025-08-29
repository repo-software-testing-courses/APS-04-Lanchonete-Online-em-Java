package Helpers;

import Helpers.EncryptadorMD5;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EncryptadorMD5Test {

    EncryptadorMD5 encryptador = new EncryptadorMD5();

    @Test
    public void testVerificarSenhaCorreta() {
        String senha = "teste123";
        String hash = encryptador.encryptar(senha);

        // A senha deve bater com seu próprio hash
        assertTrue(encryptador.verificarSenha("teste123", hash));
    }

    @Test
    public void testVerificarSenhaIncorreta() {
        String senha = "teste123";
        String hash = encryptador.encryptar(senha);

        // Uma senha diferente não deve bater com o mesmo hash
        assertFalse(encryptador.verificarSenha("senhaErrada", hash));
    }

    @Test
    public void testVerificarSenhaComHashInvalido() {
        String senha = "teste123";
        String hashInvalido = "00000000000000000000000000000000";

        // Mesmo a senha correta não deve bater com um hash incorreto
        assertFalse(encryptador.verificarSenha(senha, hashInvalido));
    }

    @Test
    public void testVerificarSenhaComNull() {
        // Testa entrada nula
        assertFalse(encryptador.verificarSenha(null, "algumHash"));
        assertFalse(encryptador.verificarSenha("algumaSenha", null));
    }

    
    @Test
    public void testHashValido() {
        String hashValido = "098f6bcd4621d373cade4e832627b4f6"; // MD5 de "test"
        assertTrue(encryptador.isHashMD5Valido(hashValido));
    }

    @Test
    public void testHashComLetrasMaiusculas() {
        String hashMaiusculo = "098F6BCD4621D373CADE4E832627B4F6";
        assertTrue(encryptador.isHashMD5Valido(hashMaiusculo));
    }

    @Test
    public void testHashInvalidoComTamanhoErrado() {
        String hashCurto = "123abc";
        assertFalse(encryptador.isHashMD5Valido(hashCurto));
    }

    @Test
    public void testHashComCaracteresInvalidos() {
        String hashComSimbolo = "098f6bcd4621d373cade4e832627b4g!"; // contém 'g' e '!'
        assertFalse(encryptador.isHashMD5Valido(hashComSimbolo));
    }

    @Test
    public void testHashNulo() {
        assertFalse(encryptador.isHashMD5Valido(null));
    }

    @Test
    public void testHashVazio() {
        assertFalse(encryptador.isHashMD5Valido(""));
    }
}