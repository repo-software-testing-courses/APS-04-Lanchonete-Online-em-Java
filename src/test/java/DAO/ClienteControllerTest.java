package DAO;

import Controllers.cadastro;
import Controllers.comprar;
import DAO.DaoCliente;
import Helpers.ValidadorCookie;
import Model.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClienteControllerTest {

    //Cadastro
    @Test
    public void CadastroTestProcessRequest_post() throws Exception {

        String json = "{"
                + "\"endereco\":{"
                +   "\"bairro\":\"Centro\","
                +   "\"cidade\":\"São Paulo\","
                +   "\"estado\":\"SP\","
                +   "\"complemento\":\"apto 101\","
                +   "\"rua\":\"Rua X\","
                +   "\"numero\":123"
                + "},"
                + "\"usuario\":{"
                +   "\"nome\":\"Daniel\","
                +   "\"sobrenome\":\"Pereira\","
                +   "\"telefone\":\"123456789\","
                +   "\"usuario\":\"danielp\","
                +   "\"senha\":\"1234\""
                + "}"
                + "}";

        // Mock do HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(
                new ByteArrayInputStream(json.getBytes("UTF-8"))));

        // Mock do HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Mock do DaoCliente
        DaoCliente daoClienteMock = mock(DaoCliente.class);

        cadastro servlet = new cadastro() {
            @Override
            protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");

                BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
                String jsonStr = br.readLine();

                JSONObject dados = new JSONObject(jsonStr);

                // Cria Endereco
                Endereco endereco = new Endereco();
                endereco.setBairro(dados.getJSONObject("endereco").getString("bairro"));
                endereco.setCidade(dados.getJSONObject("endereco").getString("cidade"));
                endereco.setEstado(dados.getJSONObject("endereco").getString("estado"));
                endereco.setComplemento(dados.getJSONObject("endereco").getString("complemento"));
                endereco.setRua(dados.getJSONObject("endereco").getString("rua"));
                endereco.setNumero(dados.getJSONObject("endereco").getInt("numero"));

                // Cria Cliente
                Cliente cliente = new Cliente();
                cliente.setNome(dados.getJSONObject("usuario").getString("nome"));
                cliente.setSobrenome(dados.getJSONObject("usuario").getString("sobrenome"));
                cliente.setTelefone(dados.getJSONObject("usuario").getString("telefone"));
                cliente.setUsuario(dados.getJSONObject("usuario").getString("usuario"));
                cliente.setSenha(dados.getJSONObject("usuario").getString("senha"));
                cliente.setFg_ativo(1);
                cliente.setEndereco(endereco);

                // Usa mock do DaoCliente
                daoClienteMock.salvar(cliente);

                PrintWriter out = resp.getWriter();
                out.println("Usuário Cadastrado!");
                out.flush();
            }
        };

        servlet.doPost(request, response);

        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(daoClienteMock, times(1)).salvar(clienteCaptor.capture());

        Cliente clienteSalvo = clienteCaptor.getValue();
        assertEquals("Daniel", clienteSalvo.getNome());
        assertEquals("Pereira", clienteSalvo.getSobrenome());
        assertEquals("123456789", clienteSalvo.getTelefone());
        assertEquals("danielp", clienteSalvo.getUsuario());
        assertEquals("1234", clienteSalvo.getSenha());
        assertEquals(1, clienteSalvo.getFg_ativo());

        Endereco enderecoSalvo = clienteSalvo.getEndereco();
        assertNotNull(enderecoSalvo);
        assertEquals("Centro", enderecoSalvo.getBairro());
        assertEquals("São Paulo", enderecoSalvo.getCidade());
        assertEquals("SP", enderecoSalvo.getEstado());
        assertEquals("apto 101", enderecoSalvo.getComplemento());
        assertEquals("Rua X", enderecoSalvo.getRua());
        assertEquals(123, enderecoSalvo.getNumero());

        printWriter.flush();
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("Usuário Cadastrado!"));
    }


    //Comprar
    @Test
    public void ComprarTestProcessRequest_comPedidoValido() throws Exception {
        // JSON de exemplo com um pedido válido
        String json = "{"
                + "\"id\":1,"
                + "\"Hamburguer\":[\"Hamburguer\",\"lanche\",2],"
                + "\"Coca\":[\"Coca\",\"bebida\",1]"
                + "}";

        // Mock do HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(
                new ByteArrayInputStream(json.getBytes("UTF-8"))));

        // Mock do HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Mock dos cookies válidos
        Cookie[] cookies = {new Cookie("valid", "true")};
        when(request.getCookies()).thenReturn(cookies);

        // Mock do ValidadorCookie
        ValidadorCookie validadorMock = mock(ValidadorCookie.class);
        when(validadorMock.validar(cookies)).thenReturn(true);

        // Mock do DaoCliente
        DaoCliente daoClienteMock = mock(DaoCliente.class);
        Cliente clienteMock = new Cliente();
        clienteMock.setId_cliente(1);
        when(daoClienteMock.pesquisaPorID("1")).thenReturn(clienteMock);

        // Mock do DaoLanche
        DaoLanche daoLancheMock = mock(DaoLanche.class);
        Lanche lancheMock = new Lanche();
        lancheMock.setNome("Hamburguer");
        lancheMock.setId_lanche(1);
        lancheMock.setValor_venda(15.00);
        when(daoLancheMock.pesquisaPorNome("Hamburguer")).thenReturn(lancheMock);

        // Mock do DaoBebida
        DaoBebida daoBebidaMock = mock(DaoBebida.class);
        Bebida bebidaMock = new Bebida();
        bebidaMock.setNome("Coca");
        bebidaMock.setId_bebida(1);
        bebidaMock.setValor_venda(5.00);
        when(daoBebidaMock.pesquisaPorNome("Coca")).thenReturn(bebidaMock);

        // Mock do DaoPedido
        DaoPedido daoPedidoMock = mock(DaoPedido.class);

        // Configuração do comportamento para pesquisaPorData
        Pedido pedidoRetornado = new Pedido();
        pedidoRetornado.setId_pedido(1);
        pedidoRetornado.setData_pedido(Instant.now().toString());
        pedidoRetornado.setValor_total(35.00);
        when(daoPedidoMock.pesquisaPorData(any(Pedido.class))).thenReturn(pedidoRetornado);

        comprar servlet = new comprar() {
            @Override
            protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");

                // Validação de cookie
                boolean resultado = false;
                try {
                    Cookie[] cookies = req.getCookies();
                    resultado = validadorMock.validar(cookies);
                } catch (java.lang.NullPointerException e) {}

                if (resultado) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
                    String jsonStr = br.readLine();
                    JSONObject dados = new JSONObject(jsonStr);

                    // Busca cliente
                    Cliente cliente = daoClienteMock.pesquisaPorID(String.valueOf(dados.getInt("id")));

                    Double valor_total = 0.00;
                    List<Lanche> lanches = new ArrayList<>();
                    List<Bebida> bebidas = new ArrayList<>();

                    // Processa itens do pedido
                    Iterator<String> keys = dados.keys();
                    while(keys.hasNext()) {
                        String nome = keys.next();
                        if(!nome.equals("id")) {
                            if(dados.getJSONArray(nome).get(1).equals("lanche")) {
                                Lanche lanche = daoLancheMock.pesquisaPorNome(nome);
                                int quantidade = dados.getJSONArray(nome).getInt(2);
                                lanche.setQuantidade(quantidade);
                                valor_total += lanche.getValor_venda() * quantidade;
                                lanches.add(lanche);
                            }
                            if(dados.getJSONArray(nome).get(1).equals("bebida")) {
                                Bebida bebida = daoBebidaMock.pesquisaPorNome(nome);
                                int quantidade = dados.getJSONArray(nome).getInt(2);
                                bebida.setQuantidade(quantidade);
                                valor_total += bebida.getValor_venda() * quantidade;
                                bebidas.add(bebida);
                            }
                        }
                    }

                    // Cria e salva pedido
                    Pedido pedido = new Pedido();
                    pedido.setData_pedido(Instant.now().toString());
                    pedido.setCliente(cliente);
                    pedido.setValor_total(valor_total);

                    // Simula o salvamento do pedido
                    daoPedidoMock.salvar(pedido);

                    // Simula a pesquisa do pedido salvo
                    Pedido pedidoSalvo = daoPedidoMock.pesquisaPorData(pedido);
                    pedidoSalvo.setCliente(cliente);

                    // Vincula itens ao pedido
                    for(Lanche lanche : lanches) {
                        daoPedidoMock.vincularLanche(pedidoSalvo, lanche);
                    }
                    for(Bebida bebida : bebidas) {
                        daoPedidoMock.vincularBebida(pedidoSalvo, bebida);
                    }

                    PrintWriter out = resp.getWriter();
                    out.println("Pedido Salvo com Sucesso!");
                    out.flush();
                } else {
                    PrintWriter out = resp.getWriter();
                    out.println("erro");
                    out.flush();
                }
            }
        };

        servlet.processarPedido(request, response);

        // Verificações
        // Verifica se o pedido foi salvo
        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(daoPedidoMock, times(1)).salvar(pedidoCaptor.capture());

        Pedido pedidoSalvo = pedidoCaptor.getValue();
        assertNotNull(pedidoSalvo);
        assertEquals(clienteMock, pedidoSalvo.getCliente());
        assertEquals(35.00, pedidoSalvo.getValor_total()); // 2x15 + 1x5 = 35

        // Verifica se os itens foram vinculados corretamente
        verify(daoPedidoMock, times(1)).vincularLanche(any(Pedido.class), any(Lanche.class));
        verify(daoPedidoMock, times(1)).vincularBebida(any(Pedido.class), any(Bebida.class));

        printWriter.flush();
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("Pedido Salvo com Sucesso!"));
    }


    @Test
    public void TestProcessRequest_comCookiesInvalidos() throws Exception {
        // Mock do HttpServletRequest com cookies inválidos
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = {new Cookie("invalid", "false")};
        when(request.getCookies()).thenReturn(cookies);

        // Mock do ValidadorCookie
        ValidadorCookie validadorMock = mock(ValidadorCookie.class);
        when(validadorMock.validar(cookies)).thenReturn(false);

        // Mock do HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        comprar servlet = new comprar() {
            @Override
            protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                boolean resultado = false;
                try {
                    Cookie[] cookies = req.getCookies();
                    resultado = validadorMock.validar(cookies);
                } catch (java.lang.NullPointerException e) {}

                if (resultado) {
                    resp.getWriter().println("sucesso");
                } else {
                    resp.getWriter().println("erro");
                }
            }
        };

        servlet.processarPedido(request, response);

        printWriter.flush();
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("erro"));
    }



    private static class DelegatingServletInputStream extends javax.servlet.ServletInputStream {
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
