package Testes.Integração;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class GetLanchesTest {

    private static final String BASE_URL = "http://localhost:8080";

    @Test
    void testeGetLanchesApi() throws Exception {
        URL url = new URL(BASE_URL + "/getLanchesCliente");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);

    }
}