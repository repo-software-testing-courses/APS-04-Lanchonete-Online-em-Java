package DAO;

import Controllers.cadastro;
import DAO.DaoCliente;
import Model.Cliente;
import Model.Endereco;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteControllerTest {

    //Cadastro
    @Test
    public void testProcessRequest_post() throws Exception {

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
