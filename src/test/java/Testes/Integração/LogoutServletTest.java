package Testes.Integração;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class LogoutServletTest {

    private static final String BASE_URL = "http://localhost:8080";

    @Test
    void testLogoutDeslogaComSucesso() throws Exception {
        URL url = new URL(BASE_URL + "/logout");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        assertEquals(200, conn.getResponseCode());

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String resposta = in.readLine();
        in.close();

        assertEquals("Deslogado", resposta);
    }
}
