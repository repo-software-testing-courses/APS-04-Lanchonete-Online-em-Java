package DAO.TestesIngredientes;
import Controllers.getIngredientes;
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
import java.util.ArrayList;
import java.util.List;
import Controllers.getIngredientesPorLancheCliente;
import com.google.gson.Gson;
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

    @InjectMocks
    getIngredientes servletGet;

    @InjectMocks
    getIngredientesPorLancheCliente servletPorLanche;

    @BeforeEach
    void setup() throws IOException {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks com @Mock/@InjectMocks
        servlet = new removerIngrediente(daoIngrediente, validadorCookie);
        servletGet = new getIngredientes(daoIngrediente, validadorCookie);
        servletPorLanche = new getIngredientesPorLancheCliente(daoIngrediente);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testGetIngredientesPorLancheCliente() throws Exception {
        // Simula um JSON com id do lanche
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("id", 8);
        byte[] jsonBytes = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);

        // Simula o corpo da requisição
        when(request.getInputStream()).thenReturn(
            new DelegatingServletInputStream(new ByteArrayInputStream(jsonBytes))
        );

        // Mocka retorno do DAO
        List<Ingrediente> ingredientes = new ArrayList<>();
        Ingrediente ing = new Ingrediente();
        ing.setId_ingrediente(3);
        ing.setNome("Alface");
        ingredientes.add(ing);

        when(daoIngrediente.listarTodosPorLanche(8)).thenReturn(ingredientes);

        servletPorLanche.doPost(request, response);

        verify(daoIngrediente).listarTodosPorLanche(8);

        String jsonEsperado = new Gson().toJson(ingredientes);
        verify(writer).print(jsonEsperado);
        verify(writer).flush();
    }

    @Test
    void testGetIngredientesComCookieValido() throws Exception {
        // Simula um cookie válido
        Cookie[] cookies = {new Cookie("token", "abc")};
        when(request.getCookies()).thenReturn(cookies);
        when(validadorCookie.validarFuncionario(cookies)).thenReturn(true);

        // Simula retorno de ingredientes
        List<Ingrediente> ingredientes = new ArrayList<>();
        Ingrediente ing = new Ingrediente();
        ing.setId_ingrediente(1);
        ing.setNome("Tomate");
        ingredientes.add(ing);

        when(daoIngrediente.listarTodos()).thenReturn(ingredientes);

        servletGet.doGet(request, response);

        verify(daoIngrediente).listarTodos();
        String jsonEsperado = new Gson().toJson(ingredientes);
        verify(writer).print(jsonEsperado);
        verify(writer).flush();
    }

    @Test
    void testGetIngredientesSemCookie() throws Exception {
        // Simula requisição sem cookies
        when(request.getCookies()).thenReturn(null);
        when(validadorCookie.validarFuncionario(null)).thenReturn(false);

        servletGet.doGet(request, response);

        // Espera resposta de erro
        verify(writer).println("erro");
    }

    @Test
    void testIngredienteRemovidoCookieValido() throws Exception {
        Cookie[] cookies = {new Cookie("token", "abc")};
        when(request.getCookies()).thenReturn(cookies);
        when(validadorCookie.validarFuncionario(cookies)).thenReturn(true);

        // Simula JSON com dados do ingrediente a remover
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("id", 10);
        jsonRequest.put("nome", "Alface");
        jsonRequest.put("descricao", "Verde");
        jsonRequest.put("quantidade", 100);
        jsonRequest.put("ValorCompra", 5.0);
        jsonRequest.put("ValorVenda", 8.0);
        jsonRequest.put("tipo", "verdura");

        byte[] jsonBytes = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
        when(request.getInputStream()).thenReturn(
            new DelegatingServletInputStream(new ByteArrayInputStream(jsonBytes))
        );

        servlet.doPost(request, response);

        // Captura o ingrediente passado ao DAO para validar seus atributos
        ArgumentCaptor<Ingrediente> captor = ArgumentCaptor.forClass(Ingrediente.class);
        verify(daoIngrediente).remover(captor.capture());
        Ingrediente ingRemovido = captor.getValue();
        Assertions.assertEquals(10, ingRemovido.getId_ingrediente());
        Assertions.assertEquals("Alface", ingRemovido.getNome());
        Assertions.assertEquals(1, ingRemovido.getFg_ativo());

        verify(writer).println("Ingrediente Alterado!");
    }

    @Test
    void testIngredienteRemovidoCookieInvalido() throws Exception {
        servlet = new removerIngrediente(daoIngrediente, validadorCookie);

        when(request.getCookies()).thenReturn(null); // Sem cookie

        // Input vazio simula POST inválido
        String emptyJson = "";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(emptyJson.getBytes(StandardCharsets.UTF_8));

        // Simula input stream customizado
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override public boolean isFinished() { return false; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener readListener) {}
            @Override public int read() throws IOException { return byteArrayInputStream.read(); }
        };

        when(request.getInputStream()).thenReturn(servletInputStream);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        // Espera mensagem de erro
        verify(writer).println("erro");
    }

    static class DelegatingServletInputStream extends javax.servlet.ServletInputStream {
        private final InputStream sourceStream;

        public DelegatingServletInputStream(InputStream sourceStream) {
            this.sourceStream = sourceStream;
        }

        @Override public int read() throws IOException {
            return sourceStream.read();
        }

        @Override public boolean isFinished() {
            try {
                return sourceStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override public boolean isReady() { return true; }

        @Override public void setReadListener(javax.servlet.ReadListener readListener) {}
    }
}
