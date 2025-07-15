package Integracao;


import org.junit.jupiter.api.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.UUID;


public class LoginIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lanchonete";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin123";

    private String testUsername;
    private String testPassword;

    @BeforeEach
    void PrepararUsuarioTeste() throws SQLException {
        testUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        testPassword = "testpass123";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO tb_clientes(usuario, senha, nome, sobrenome, telefone, fg_ativo) " +
                             "VALUES(?, MD5(?), 'Test', 'User', '11999999999', 1)")) {

            stmt.setString(1, testUsername);
            stmt.setString(2, testPassword);
            stmt.executeUpdate();
        }
    }

    @Test
    @DisplayName("Login com credenciais válidas")
    void TestarLoginComCredenciaisValidas() throws IOException {
        String json = String.format("{\"usuario\":\"%s\",\"senha\":\"%s\"}", testUsername, testPassword);
        HttpURLConnection conn = CriarRequisicaoLogin(json);

        ValidarRespostaLoginSucesso(conn);
        ValidarCookieResposta(conn);
    }

    @Test
    @DisplayName("Login com credenciais inválidas")
    void TestarLoginComCredenciaisInvalidas() throws IOException {
        String json = String.format("{\"usuario\":\"%s\",\"senha\":\"senhaerrada\"}", testUsername);
        HttpURLConnection conn = CriarRequisicaoLogin(json);

        ValidarRespostaLoginErro(conn);
    }

    @Test
    @DisplayName("Login com usuário inativo")
    void TestarLoginComUsuarioInativo() throws IOException, SQLException {
        // Cria usuário inativo
        String inactiveUser = "inactive_" + UUID.randomUUID().toString().substring(0, 8);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO tb_clientes(usuario, senha, nome, sobrenome, telefone, fg_ativo) " +
                             "VALUES(?, MD5(?), 'Inativo', 'User', '11999999999', 0)")) {

            stmt.setString(1, inactiveUser);
            stmt.setString(2, "testpass");
            stmt.executeUpdate();
        }

        String json = String.format("{\"usuario\":\"%s\",\"senha\":\"testpass\"}", inactiveUser);
        HttpURLConnection conn = CriarRequisicaoLogin(json);

        ValidarRespostaLoginErro(conn);
    }

    private HttpURLConnection CriarRequisicaoLogin(String json) throws IOException {
        URL url = new URL(BASE_URL + "/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return conn;
    }

    private void ValidarRespostaLoginSucesso(HttpURLConnection conn) throws IOException {
        Assertions.assertEquals(200, conn.getResponseCode(), "Status HTTP deveria ser 200");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = br.readLine();
            Assertions.assertTrue(response.contains("../carrinho/carrinho.html"),
                    "Deveria redirecionar para carrinho");
        }
    }

    private void ValidarRespostaLoginErro(HttpURLConnection conn) throws IOException {
        Assertions.assertEquals(200, conn.getResponseCode(), "Status HTTP deveria ser 200");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = br.readLine();
            Assertions.assertEquals("erro", response, "Deveria retornar 'erro'");
        }
    }

    private void ValidarCookieResposta(HttpURLConnection conn) {
        String cookieHeader = conn.getHeaderField("Set-Cookie");
        Assertions.assertNotNull(cookieHeader, "Deveria retornar cookie");
        Assertions.assertTrue(cookieHeader.contains("token="), "Cookie deveria conter token");
    }

    @AfterEach
    void LimparDadosTeste() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            int clientesRemovidos = stmt.executeUpdate(
                    "DELETE FROM tb_clientes WHERE usuario LIKE 'testuser_%' OR usuario LIKE 'inactive_%'");

            int tokensRemovidos = stmt.executeUpdate(
                    "DELETE FROM tb_tokens WHERE TRUE");

            System.out.printf(
                    "Limpeza realizada: %d clientes e %d tokens removidos%n",
                    clientesRemovidos, tokensRemovidos);
        }
    }

}
