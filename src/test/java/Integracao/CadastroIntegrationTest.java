package Integracao;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.UUID;

public class CadastroIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lanchonete";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin123";

    @Test
    public void testCadastroClienteCompleto() throws IOException, SQLException {

        String uniqueUser = "test_" + UUID.randomUUID().toString().substring(0, 8);

        String json = String.format("{"
                + "\"endereco\":{"
                +   "\"bairro\":\"Teste\","
                +   "\"cidade\":\"Teste\","
                +   "\"estado\":\"TS\","
                +   "\"complemento\":\"teste\","
                +   "\"rua\":\"Rua Teste\","
                +   "\"numero\":123"
                + "},"
                + "\"usuario\":{"
                +   "\"nome\":\"Teste\","
                +   "\"sobrenome\":\"Teste\","
                +   "\"telefone\":\"11999999999\","
                +   "\"usuario\":\"%s\","
                +   "\"senha\":\"123456\""
                + "}"
                + "}", uniqueUser);

        URL url = new URL(BASE_URL + "/cadastro");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        Assertions.assertEquals(200, conn.getResponseCode(), "Deveria retornar status 200");

        try (Connection dbConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = dbConn.prepareStatement(
                     "SELECT c.*, e.bairro FROM tb_clientes c JOIN tb_enderecos e ON c.id_endereco = e.id_endereco WHERE c.usuario = ?")) {

            stmt.setString(1, uniqueUser);
            ResultSet rs = stmt.executeQuery();

            Assertions.assertTrue(rs.next(), "Cliente deveria estar persistido no banco");
            Assertions.assertEquals("Teste", rs.getString("nome"));
            Assertions.assertEquals("Teste", rs.getString("bairro"));
        }
    }

    @AfterEach
    void cleanTestData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM tb_clientes WHERE usuario LIKE 'test_%'");
            stmt.executeUpdate("DELETE FROM tb_enderecos WHERE bairro = 'Teste' AND "
                    + "NOT EXISTS (SELECT 1 FROM tb_clientes WHERE tb_clientes.id_endereco = tb_enderecos.id_endereco)");
        }
    }

}
