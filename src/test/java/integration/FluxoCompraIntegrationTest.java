package integration;

import DAO.*;
import Model.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoCompraIntegrationTest {

    private DaoCliente daoCliente;
    private DaoToken daoToken;
    private DaoLanche daoLanche;
    private DaoBebida daoBebida;
    private DaoPedido daoPedido;
    private static final String USUARIO_TESTE = "cliente_compra_test";
    private static final String SENHA_TESTE = "senha123";
    private static Cliente clienteTeste;
    private static String tokenTeste;
    private static Lanche lancheTeste;
    private static Bebida bebidaTeste;

    @BeforeEach
    void setUp() {
        daoCliente = new DaoCliente();
        daoToken = new DaoToken();
        daoLanche = new DaoLanche();
        daoBebida = new DaoBebida();
        daoPedido = new DaoPedido();
        limparDadosTeste();
    }

    @Test
    @Order(1)
    void prepararAmbienteCompra() {
        Endereco endereco = criarEnderecoTeste();
        clienteTeste = criarClienteTeste(endereco);
        
        assertDoesNotThrow(() -> daoCliente.salvar(clienteTeste));
        
        Cliente clienteSalvo = daoCliente.pesquisaPorUsuario(clienteTeste);
        assertNotNull(clienteSalvo);
        assertTrue(clienteSalvo.getId_cliente() > 0);
        clienteTeste = clienteSalvo;

        lancheTeste = criarLancheTeste();
        assertDoesNotThrow(() -> daoLanche.salvar(lancheTeste));
        
        Lanche lancheRecuperado = daoLanche.pesquisaPorNome(lancheTeste.getNome());
        assertNotNull(lancheRecuperado);
        assertEquals(lancheTeste.getNome(), lancheRecuperado.getNome());
        lancheTeste = lancheRecuperado;

        bebidaTeste = criarBebidaTeste();
        assertDoesNotThrow(() -> daoBebida.salvar(bebidaTeste));
        
        Bebida bebidaRecuperada = daoBebida.pesquisaPorNome(bebidaTeste.getNome());
        assertNotNull(bebidaRecuperada);
        assertEquals(bebidaTeste.getNome(), bebidaRecuperada.getNome());
        bebidaTeste = bebidaRecuperada;

        Cliente clienteLogin = new Cliente();
        clienteLogin.setUsuario(USUARIO_TESTE);
        clienteLogin.setSenha(SENHA_TESTE);
        
        boolean loginSuccess = daoCliente.login(clienteLogin);
        assertTrue(loginSuccess);

        tokenTeste = clienteTeste.getId_cliente() + "-" + Instant.now().toString();
        daoToken.salvar(tokenTeste);
        
        boolean tokenValido = daoToken.validar(tokenTeste);
        assertTrue(tokenValido);
    }

    @Test
    @Order(2)
    void consultarProdutosDisponiveis() {
        
        List<Lanche> lanches = daoLanche.listarTodos();
        assertNotNull(lanches);
        assertFalse(lanches.isEmpty());
        
        boolean lancheEncontrado = lanches.stream()
            .anyMatch(l -> l.getNome().equals(lancheTeste.getNome()));
        assertTrue(lancheEncontrado);

        List<Bebida> bebidas = daoBebida.listarTodos();
        assertNotNull(bebidas);
        assertFalse(bebidas.isEmpty());
        
        boolean bebidaEncontrada = bebidas.stream()
            .anyMatch(b -> b.getNome().equals(bebidaTeste.getNome()));
        assertTrue(bebidaEncontrada);
    }

    @Test
    @Order(3)
    void simularCarrinhoCompra() {
        int quantidadeLanche = 2;
        int quantidadeBebida = 3;
        
        double valorLanches = lancheTeste.getValor_venda() * quantidadeLanche;
        double valorBebidas = bebidaTeste.getValor_venda() * quantidadeBebida;
        double valorTotalEsperado = valorLanches + valorBebidas;
        
        assertTrue(valorTotalEsperado > 0);
        assertEquals(valorLanches + valorBebidas, valorTotalEsperado, 0.01);
    }

    @Test
    @Order(4)
    void finalizarCompra() {
        
        boolean tokenValido = daoToken.validar(tokenTeste);
        assertTrue(tokenValido);

        Cliente cliente = daoCliente.pesquisaPorID(String.valueOf(clienteTeste.getId_cliente()));
        assertNotNull(cliente);
        assertEquals(clienteTeste.getId_cliente(), cliente.getId_cliente());

        int quantidadeLanche = 2;
        int quantidadeBebida = 1;
        
        lancheTeste.setQuantidade(quantidadeLanche);
        bebidaTeste.setQuantidade(quantidadeBebida);
        
        double valorTotal = (lancheTeste.getValor_venda() * quantidadeLanche) + 
                           (bebidaTeste.getValor_venda() * quantidadeBebida);

        Pedido pedido = new Pedido();
        pedido.setData_pedido(Instant.now().toString());
        pedido.setCliente(cliente);
        pedido.setValor_total(valorTotal);
        
        assertDoesNotThrow(() -> daoPedido.salvar(pedido));

        Pedido pedidoSalvo = daoPedido.pesquisaPorData(pedido);
        assertNotNull(pedidoSalvo);
        assertTrue(pedidoSalvo.getId_pedido() > 0);
        assertEquals(valorTotal, pedidoSalvo.getValor_total(), 0.01);

        assertDoesNotThrow(() -> daoPedido.vincularLanche(pedidoSalvo, lancheTeste));

        assertDoesNotThrow(() -> daoPedido.vincularBebida(pedidoSalvo, bebidaTeste));
    }

    @Test
    @Order(5)
    void verificarPersistenciaPedido() {
        try (Connection conn = new DaoUtil().conecta()) {
            PreparedStatement stmtPedido = conn.prepareStatement(
                "SELECT * FROM tb_pedidos WHERE id_cliente = ? ORDER BY id_pedido DESC LIMIT 1"
            );
            stmtPedido.setInt(1, clienteTeste.getId_cliente());
            ResultSet rsPedido = stmtPedido.executeQuery();
            
            assertTrue(rsPedido.next());
            int idPedido = rsPedido.getInt("id_pedido");
            double valorTotal = rsPedido.getDouble("valor_total");
            
            assertTrue(idPedido > 0);
            assertTrue(valorTotal > 0);

            PreparedStatement stmtLanche = conn.prepareStatement(
                "SELECT * FROM tb_lanches_pedido WHERE id_pedido = ? AND id_lanche = ?"
            );
            stmtLanche.setInt(1, idPedido);
            stmtLanche.setInt(2, lancheTeste.getId_lanche());
            ResultSet rsLanche = stmtLanche.executeQuery();
            
            assertTrue(rsLanche.next());
            assertEquals(2, rsLanche.getInt("quantidade"));

            PreparedStatement stmtBebida = conn.prepareStatement(
                "SELECT * FROM tb_bebidas_pedido WHERE id_pedido = ? AND id_bebida = ?"
            );
            stmtBebida.setInt(1, idPedido);
            stmtBebida.setInt(2, bebidaTeste.getId_bebida());
            ResultSet rsBebida = stmtBebida.executeQuery();
            
            assertTrue(rsBebida.next());
            assertEquals(1, rsBebida.getInt("quantidade"));

        } catch (SQLException e) {
            fail("Erro ao verificar persistência: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void fluxoCompletoEndToEnd() {
        
        assertTrue(daoToken.validar(tokenTeste));

        Cliente cliente = daoCliente.pesquisaPorID(String.valueOf(clienteTeste.getId_cliente()));
        assertNotNull(cliente);

        int quantidadeLanche = 1;
        int quantidadeBebida = 2;
        
        double valorTotal = 0.0;
        
        Lanche lanche = daoLanche.pesquisaPorNome(lancheTeste.getNome());
        lanche.setQuantidade(quantidadeLanche);
        valorTotal += lanche.getValor_venda() * quantidadeLanche;
        
        Bebida bebida = daoBebida.pesquisaPorNome(bebidaTeste.getNome());
        bebida.setQuantidade(quantidadeBebida);
        valorTotal += bebida.getValor_venda() * quantidadeBebida;

        Pedido pedido = new Pedido();
        pedido.setData_pedido(Instant.now().toString());
        pedido.setCliente(cliente);
        pedido.setValor_total(valorTotal);
        
        daoPedido.salvar(pedido);
        
        Pedido pedidoComId = daoPedido.pesquisaPorData(pedido);
        pedidoComId.setCliente(cliente);
        
        daoPedido.vincularLanche(pedidoComId, lanche);
        daoPedido.vincularBebida(pedidoComId, bebida);

        assertTrue(pedidoComId.getId_pedido() > 0);
        assertEquals(valorTotal, pedidoComId.getValor_total(), 0.01);
        assertNotNull(pedidoComId.getData_pedido());
    }

    @Test
    @Order(7)
    void testeComprarSemTokenValido() {
        daoToken.remover(tokenTeste);
        
        boolean tokenValido = daoToken.validar(tokenTeste);
        assertFalse(tokenValido);
        
        Pedido pedido = new Pedido();
        pedido.setData_pedido(Instant.now().toString());
        pedido.setCliente(clienteTeste);
        pedido.setValor_total(50.0);
        
        assertDoesNotThrow(() -> daoPedido.salvar(pedido));
    }


    private void limparDadosTeste() {
        try (Connection conn = new DaoUtil().conecta()) {
            conn.createStatement().execute("DELETE FROM tb_lanches_pedido WHERE id_pedido IN (SELECT id_pedido FROM tb_pedidos WHERE id_cliente IN (SELECT id_cliente FROM tb_clientes WHERE usuario = '" + USUARIO_TESTE + "'))");
            conn.createStatement().execute("DELETE FROM tb_bebidas_pedido WHERE id_pedido IN (SELECT id_pedido FROM tb_pedidos WHERE id_cliente IN (SELECT id_cliente FROM tb_clientes WHERE usuario = '" + USUARIO_TESTE + "'))");
            conn.createStatement().execute("DELETE FROM tb_pedidos WHERE id_cliente IN (SELECT id_cliente FROM tb_clientes WHERE usuario = '" + USUARIO_TESTE + "')");
            conn.createStatement().execute("DELETE FROM tb_tokens WHERE token LIKE '" + USUARIO_TESTE + "%'");
            conn.createStatement().execute("DELETE FROM tb_clientes WHERE usuario = '" + USUARIO_TESTE + "'");
            conn.createStatement().execute("DELETE FROM tb_lanches WHERE nm_lanche = 'nomeLanche'");
            conn.createStatement().execute("DELETE FROM tb_bebidas WHERE nm_bebida = 'nomeBebida'");
        } catch (SQLException e) {
        }
    }

    private Endereco criarEnderecoTeste() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua");
        endereco.setBairro("Bairro");
        endereco.setNumero(123);
        endereco.setComplemento("Complemento");
        endereco.setCidade("Rio de Janeiro");
        endereco.setEstado("RJ");
        return endereco;
    }

    private Cliente criarClienteTeste(Endereco endereco) {
        Cliente cliente = new Cliente();
        cliente.setNome("nomeCliente");
        cliente.setSobrenome("sobrenomeCliente");
        cliente.setTelefone("21999999999");
        cliente.setUsuario(USUARIO_TESTE);
        cliente.setSenha(SENHA_TESTE);
        cliente.setFg_ativo(1);
        cliente.setEndereco(endereco);
        return cliente;
    }

    private Lanche criarLancheTeste() {
        Lanche lanche = new Lanche();
        lanche.setNome("nomeLanche");
        lanche.setDescricao("descricaoTeste");
        lanche.setValor_venda(25.90);
        lanche.setFg_ativo(1);
        return lanche;
    }

    private Bebida criarBebidaTeste() {
        Bebida bebida = new Bebida();
        bebida.setNome("nomeBebida");
        bebida.setDescricao("bebidaTeste");
        bebida.setQuantidade(10);
        bebida.setValor_compra(3.00);
        bebida.setValor_venda(8.50);
        bebida.setTipo("Refrigerante");
        bebida.setFg_ativo(1);
        return bebida;
    }
}
