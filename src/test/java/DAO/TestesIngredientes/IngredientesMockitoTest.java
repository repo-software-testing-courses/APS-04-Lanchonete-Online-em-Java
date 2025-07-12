package DAO.TestesIngredientes;
import Controllers.removerIngrediente;
import DAO.DaoIngrediente;
import Helpers.ValidadorCookie;
import Model.Ingrediente;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class IngredientesMockitoTest {

    @InjectMocks
    removerIngrediente servlet;

    @Mock
    DaoIngrediente daoIngrediente;

    @Mock
    ValidadorCookie validadorCookie;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    PrintWriter writer;

    @BeforeEach
    void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        servlet = new removerIngrediente(daoIngrediente, validadorCookie);

        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testIngredienteRemovidoCookievalido() throws Exception {
        Cookie[] cookies = {new Cookie("token", "abc")};
        when(request.getCookies()).thenReturn(cookies);
        when(validadorCookie.validarFuncionario(cookies)).thenReturn(true);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("id", 10);
        jsonRequest.put("nome", "Alface");
        jsonRequest.put("descricao", "Verde");
        jsonRequest.put("quantidade", 100);
        jsonRequest.put("ValorCompra", 5.0);
        jsonRequest.put("ValorVenda", 8.0);
        jsonRequest.put("tipo", "verdura");

        byte[] jsonBytes = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);

        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(jsonBytes)));

        servlet.doPost(request, response);

        ArgumentCaptor<Ingrediente> captor = ArgumentCaptor.forClass(Ingrediente.class);
        verify(daoIngrediente).remover(captor.capture());
        Ingrediente ingRemovido = captor.getValue();
        Assertions.assertEquals(10, ingRemovido.getId_ingrediente());
        Assertions.assertEquals("Alface", ingRemovido.getNome());
        Assertions.assertEquals(1, ingRemovido.getFg_ativo());

        verify(writer).println("Ingrediente Alterado!");
    }

@Test
void estIngredienteRemovidoCookievalido() throws Exception {
    servlet = new removerIngrediente(daoIngrediente, validadorCookie);

    when(request.getCookies()).thenReturn(null);

    // InputStream vazio
    String emptyJson = "";
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(emptyJson.getBytes(StandardCharsets.UTF_8));

    ServletInputStream servletInputStream = new ServletInputStream() {
        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {}

        @Override
        public int read() throws IOException {
            return byteArrayInputStream.read();
        }
    };

    when(request.getInputStream()).thenReturn(servletInputStream);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    // Valida que a resposta foi "erro"
    verify(writer).println("erro");
}

    static class DelegatingServletInputStream extends javax.servlet.ServletInputStream {

        private final InputStream sourceStream;

        public DelegatingServletInputStream(InputStream sourceStream) {
            this.sourceStream = sourceStream;
        }

        @Override
        public int read() throws IOException {
            return sourceStream.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return sourceStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(javax.servlet.ReadListener readListener) {

        }
    }
}
