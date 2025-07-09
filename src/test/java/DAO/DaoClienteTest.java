package DAO;

import Model.Cliente;
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
    void testSalvarCliente() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setSobrenome("Silva");
        cliente.setTelefone("999999999");
        cliente.setUsuario("joao_teste");
        cliente.setSenha("123");
        cliente.setFg_ativo(1);

        assertDoesNotThrow(() -> daoCliente.salvar(cliente));

        // Validando que foi salvo corretamente no banco
        Cliente clienteSalvo = daoCliente.pesquisaPorUsuario(cliente);
        assertNotNull(clienteSalvo);
        assertEquals("João", clienteSalvo.getNome());
    }

    @Test
    void testListarTodos() throws Exception {
        List<Cliente> clientes = daoCliente.listarTodos();
        assertNotNull(clientes);
        assertTrue(clientes.size() >= 0);
    }

    @Test
    void testPesquisaPorUsuario() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setUsuario("joao_teste");

        Cliente result = daoCliente.pesquisaPorUsuario(cliente);
        assertNotNull(result);
        assertEquals("joao_teste", result.getUsuario());
    }

    @Test
    void testPesquisaPorID() throws Exception {
        // Primeiro insere um cliente para garantir ID conhecido
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");
        cliente.setSobrenome("Souza");
        cliente.setTelefone("88888888");
        cliente.setUsuario("maria_teste");
        cliente.setSenha("123");
        cliente.setFg_ativo(1);
        daoCliente.salvar(cliente);

        Cliente clienteInserido = daoCliente.pesquisaPorUsuario(cliente);
        assertNotNull(clienteInserido);

        Cliente clientePorID = daoCliente.pesquisaPorID(String.valueOf(clienteInserido.getId_cliente()));
        assertNotNull(clientePorID);
        assertEquals("Maria", clientePorID.getNome());
    }

    @Test
    void testLoginValido() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Carlos");
        cliente.setSobrenome("Almeida");
        cliente.setTelefone("77777777");
        cliente.setUsuario("carlos_teste");
        cliente.setSenha("123");
        cliente.setFg_ativo(1);
        daoCliente.salvar(cliente);

        Cliente loginCliente = new Cliente();
        loginCliente.setUsuario("carlos_teste");
        loginCliente.setSenha("123");

        boolean result = daoCliente.login(loginCliente);
        assertTrue(result);
    }

    @Test
    void testLoginInvalido() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setUsuario("nao_existe");
        cliente.setSenha("senhaErrada");

        boolean result = daoCliente.login(cliente);
        assertFalse(result);
    }

}
