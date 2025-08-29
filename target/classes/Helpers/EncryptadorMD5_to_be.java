package Helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
/**
 * Classe para criptografar senhas usando o algoritmo MD5.
 * 
 * Esta classe fornece um método para criptografar uma senha em formato de hash MD5.
 * O método garante que a senha seja convertida corretamente em um hash de 32 caracteres.
 */
public class EncryptadorMD5_to_be {

public String encryptar(String senha){
    if (senha == null) {
        throw new IllegalArgumentException("Senha não pode ser nula");
    }

    try {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(senha.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;

    } catch (Exception e) {
        throw new RuntimeException("Erro ao gerar hash MD5", e); // lança uma exceção se ocorrer um erro
    }
}
}

