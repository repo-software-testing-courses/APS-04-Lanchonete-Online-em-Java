package DAO;

import Model.Cliente;
import Model.Endereco;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DaoClienteTest {

    private DaoCliente daoCliente;

    @BeforeEach
    void setUp() throws Exception {
        daoCliente = new DaoCliente();
    }

    @Test
    void TestSalvarClienteComNovoEndereco() {
        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setSobrenome("Silva");
        cliente.setTelefone("11999999999");
        cliente.setUsuario("joao_test");
        cliente.setSenha("senha123");
        cliente.setFg_ativo(1);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setBairro("Centro");
        endereco.setNumero(100);
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        assertDoesNotThrow(() -> daoCliente.salvar(cliente));

        // Validando que foi salvo corretamente no banco
        Cliente clienteSalvo = daoCliente.pesquisaPorUsuario(cliente);
        assertNotNull(clienteSalvo);
        assertEquals("João", clienteSalvo.getNome());
    }

    @Test
    void TestSalvarClienteComEnderecoExistente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");
        cliente.setSobrenome("Souza");
        cliente.setTelefone("11988888888");
        cliente.setUsuario("maria_test");
        cliente.setSenha("senha123");
        cliente.setFg_ativo(1);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setBairro("Centro");
        endereco.setNumero(100);
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        // Salva primeiro para existir
        daoCliente.salvar(cliente);

        // Salva novamente para entrar no else do salvar
        assertDoesNotThrow(() -> daoCliente.salvar(cliente));
    }

    @Test
    void TestListarTodosClientes() {
        List<Cliente> clientes = daoCliente.listarTodos();
        assertNotNull(clientes);
        assertTrue(clientes.size() >= 0);
    }

    @Test
    void TestPesquisaPorUsuarioExistente() {
        Cliente cliente = new Cliente();
        cliente.setUsuario("joao_test");
        Cliente resultado = daoCliente.pesquisaPorUsuario(cliente);
        assertEquals("joao_test", resultado.getUsuario());
    }

    @Test
    void TestPesquisaPorUsuarioInexistente() {
        Cliente cliente = new Cliente();
        cliente.setUsuario("usuario_inexistente");
        Cliente resultado = daoCliente.pesquisaPorUsuario(cliente);
        assertNull(resultado.getUsuario());
    }

    @Test
    void TestPesquisaPorIDExistente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Carlos");
        cliente.setSobrenome("Almeida");
        cliente.setTelefone("77777777");
        cliente.setUsuario("carlos_test");
        cliente.setSenha("senha123");
        cliente.setFg_ativo(1);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setBairro("Centro");
        endereco.setNumero(200);
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        daoCliente.salvar(cliente);

        Cliente clienteInserido = daoCliente.pesquisaPorUsuario(cliente);
        assertNotNull(clienteInserido);

        Cliente clientePorID = daoCliente.pesquisaPorID(String.valueOf(clienteInserido.getId_cliente()));
        assertNotNull(clientePorID);
        assertEquals("Carlos", clientePorID.getNome());
    }

    @Test
    void TestPesquisaPorIDInexistente() {
        Cliente cliente = daoCliente.pesquisaPorID("-1");
        assertNotNull(cliente);
        assertEquals(0, cliente.getId_cliente()); // pois retorna new Cliente()
    }

    @Test
    void TestLoginValido() {
        Cliente cliente = new Cliente();
        cliente.setNome("Pedro");
        cliente.setSobrenome("Mendes");
        cliente.setTelefone("66666666");
        cliente.setUsuario("pedro_test");
        cliente.setSenha("senha123");
        cliente.setFg_ativo(1);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setBairro("Centro");
        endereco.setNumero(300);
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        daoCliente.salvar(cliente);

        Cliente loginCliente = new Cliente();
        loginCliente.setUsuario("pedro_test");
        loginCliente.setSenha("senha123");

        boolean result = daoCliente.login(loginCliente);
        assertTrue(result);
    }

    @Test
    void TestLoginSenhaInvalida() {
        Cliente cliente = new Cliente();
        cliente.setUsuario("joao_test");
        cliente.setSenha("senha_errada");

        boolean result = daoCliente.login(cliente);
        assertFalse(result);
    }

    @Test
    void TestLoginUsuarioInexistente() {
        Cliente cliente = new Cliente();
        cliente.setUsuario("usuario_inexistente");
        cliente.setSenha("qualquer");

        boolean result = daoCliente.login(cliente);
        assertFalse(result);
    }

}
