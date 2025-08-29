package Testes.Unitários;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import Helpers.EncryptadorMD5;

public class EncryptadorMD5Test {

    @Test
    public void testEncryptarSenhaSimples() {
        EncryptadorMD5 encryptador = new EncryptadorMD5();
        String senha = "123456";
        String esperado = "e10adc3949ba59abbe56e057f20f883e";
        String resultado = encryptador.encryptar(senha);
        assertEquals(esperado, resultado);
    }

    @Test
    public void testEncryptarSenhaVazia() {
        EncryptadorMD5 encryptador = new EncryptadorMD5();
        String senha = "";
        String esperado = "d41d8cd98f00b204e9800998ecf8427e";
        String resultado = encryptador.encryptar(senha);
        assertEquals(esperado, resultado);
    }

    @Test
    public void testEncryptarSenhaComCaracteresEspeciais() {
        EncryptadorMD5 encryptador = new EncryptadorMD5();
        String senha = "senha@123";
        String esperado = "ada3c39413b4f6284c8301257812190e";  // <-- hash correto
        String resultado = encryptador.encryptar(senha);
        System.err.println(encryptador.encryptar(senha));
        assertEquals(esperado, resultado);
    }


    @Test
    public void testEncryptarSenhaNull() {
        EncryptadorMD5 encryptador = new EncryptadorMD5();
        String resultado = encryptador.encryptar(null);
        assertNull(resultado);  // o método atual retorna null em caso de exceção
    }
}