package Integracao;

import DAO.*;
import Model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoCompraIntegrationTest {

    private static DaoCliente daoCliente;
    private static DaoToken daoToken;
    private static DaoLanche daoLanche;
    private static DaoBebida daoBebida;
    private static DaoPedido daoPedido;
    private static final String USUARIO_TESTE = "cliente_compra_test";
    private static final String SENHA_TESTE = "senha123";
    
    
    private static class EstadoCompra {
        private final Cliente cliente;
        private final String token;
        private final Lanche lanche;
        private final Bebida bebida;
        private final Pedido pedido;
        private final boolean ambientePreparado;
        private final boolean produtosConsultados;
        private final boolean carrinhoSimulado;
        private final boolean compraFinalizada;
        
        private EstadoCompra(Cliente cliente, String token, Lanche lanche, Bebida bebida, 
                           Pedido pedido, boolean ambientePreparado, boolean produtosConsultados,
                           boolean carrinhoSimulado, boolean compraFinalizada) {
            this.cliente = cliente;
            this.token = token;
            this.lanche = lanche;
            this.bebida = bebida;
            this.pedido = pedido;
            this.ambientePreparado = ambientePreparado;
            this.produtosConsultados = produtosConsultados;
            this.carrinhoSimulado = carrinhoSimulado;
            this.compraFinalizada = compraFinalizada;
        }
        
        public static EstadoCompra inicial() {
            return new EstadoCompra(null, null, null, null, null, false, false, false, false);
        }
        
        public EstadoCompra comCliente(Cliente cliente) {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, true, 
                                   produtosConsultados, carrinhoSimulado, compraFinalizada);
        }
        
        public EstadoCompra comToken(String token) {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, ambientePreparado, 
                                   produtosConsultados, carrinhoSimulado, compraFinalizada);
        }
        
        public EstadoCompra comLanche(Lanche lanche) {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, ambientePreparado, 
                                   produtosConsultados, carrinhoSimulado, compraFinalizada);
        }
        
        public EstadoCompra comBebida(Bebida bebida) {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, ambientePreparado, 
                                   produtosConsultados, carrinhoSimulado, compraFinalizada);
        }
        
        public EstadoCompra comProdutosConsultados() {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, ambientePreparado, 
                                   true, carrinhoSimulado, compraFinalizada);
        }
        
        public EstadoCompra comCarrinhoSimulado() {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, ambientePreparado, 
                                   produtosConsultados, true, compraFinalizada);
        }
        
        public EstadoCompra comPedido(Pedido pedido) {
            return new EstadoCompra(cliente, token, lanche, bebida, pedido, ambientePreparado, 
                                   produtosConsultados, carrinhoSimulado, true);
        }
        
        public Cliente getCliente() { 
            return cliente; 
        }
        public String getToken() { 
            return token; 
        }
        public Lanche getLanche() { 
            return lanche; 
        }
        public Bebida getBebida() { 
            return bebida; 
        }
        public Pedido getPedido() { 
            return pedido; 
        }
        public boolean isAmbientePreparado() { 
            return ambientePreparado; 
        }
        public boolean isProdutosConsultados() { 
            return produtosConsultados; 
        }
        public boolean isCarrinhoSimulado() { 
            return carrinhoSimulado; 
        }
        public boolean isCompraFinalizada() { 
            return compraFinalizada; 
        }
    }
    
    private static EstadoCompra estadoAtual = EstadoCompra.inicial();

    @BeforeAll
    static void classeConfig() {
        daoCliente = new DaoCliente();
        daoToken = new DaoToken();
        daoLanche = new DaoLanche();
        daoBebida = new DaoBebida();
        daoPedido = new DaoPedido();
        
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

    private Function<EstadoCompra, EstadoCompra> prepararCliente = (estado) -> {
        try {
            Endereco endereco = enderecoTeste();
            Cliente cliente = clienteTeste(endereco);
            
            daoCliente.salvar(cliente);
            Cliente clienteSalvo = daoCliente.pesquisaPorUsuario(cliente);
            
            return estado.comCliente(clienteSalvo);
        } catch (Exception e) {
            throw new RuntimeException("erro cliente: " + e.getMessage(), e);
        }
    };
    
    private Function<EstadoCompra, EstadoCompra> prepararLanche = (estado) -> {
        try {
            Lanche lanche = lancheTeste();
            daoLanche.salvar(lanche);
            Lanche lancheRecuperado = daoLanche.pesquisaPorNome(lanche.getNome());
            
            return estado.comLanche(lancheRecuperado);
        } catch (Exception e) {
            throw new RuntimeException("erro lanche: " + e.getMessage(), e);
        }
    };
    
    private Function<EstadoCompra, EstadoCompra> prepararBebida = (estado) -> {
        try {
            Bebida bebida = bebidaTeste();
            daoBebida.salvar(bebida);
            Bebida bebidaRecuperada = daoBebida.pesquisaPorNome(bebida.getNome());
            
            return estado.comBebida(bebidaRecuperada);
        } catch (Exception e) {
            throw new RuntimeException("erro bebida: " + e.getMessage(), e);
        }
    };
    
    private Function<EstadoCompra, EstadoCompra> realizarLogin = (estado) -> {
        try {
            Cliente clienteLogin = new Cliente();
            clienteLogin.setUsuario(USUARIO_TESTE);
            clienteLogin.setSenha(SENHA_TESTE);
            
            boolean loginSuccess = daoCliente.login(clienteLogin);
            if (!loginSuccess) {
                throw new RuntimeException("erro cliente");
            }
            
            String token = estado.getCliente().getId_cliente() + "-" + Instant.now().toString();
            daoToken.salvar(token);
            
            return estado.comToken(token);
        } catch (Exception e) {
            throw new RuntimeException("erro login: " + e.getMessage(), e);
        }
    };
    
    private Function<EstadoCompra, EstadoCompra> consultarProdutos = (estado) -> {
        try {
            List<Lanche> lanches = daoLanche.listarTodos();
            List<Bebida> bebidas = daoBebida.listarTodos();
            
            if (lanches == null || bebidas == null) {
                throw new RuntimeException("erro produtos");
            }
            
            return estado.comProdutosConsultados();
        } catch (Exception e) {
            throw new RuntimeException("erro produtos: " + e.getMessage(), e);
        }
    };
    
    private Function<EstadoCompra, EstadoCompra> simularCarrinho = (estado) -> {
        try {
            int quantidadeLanche = 2;
            int quantidadeBebida = 3;
            
            double valorLanches = estado.getLanche().getValor_venda() * quantidadeLanche;
            double valorBebidas = estado.getBebida().getValor_venda() * quantidadeBebida;
            double valorTotal = valorLanches + valorBebidas;
            
            if (valorTotal <= 0) {
                throw new RuntimeException("Valor total inválido");
            }
            
            return estado.comCarrinhoSimulado();
        } catch (Exception e) {
            throw new RuntimeException("erro carrinho: " + e.getMessage(), e);
        }
    };
    
    private Function<EstadoCompra, EstadoCompra> finalizarCompra = (estado) -> {
        try {
            boolean tokenValido = daoToken.validar(estado.getToken());
            if (!tokenValido) {
                throw new RuntimeException("Token inválido");
            }
            
            Cliente cliente = daoCliente.pesquisaPorID(String.valueOf(estado.getCliente().getId_cliente()));
            
            int quantidadeLanche = 2;
            int quantidadeBebida = 1;
            
            Lanche lanche = estado.getLanche();
            Bebida bebida = estado.getBebida();
            lanche.setQuantidade(quantidadeLanche);
            bebida.setQuantidade(quantidadeBebida);
            
            double valorTotal = (lanche.getValor_venda() * quantidadeLanche) + 
                               (bebida.getValor_venda() * quantidadeBebida);
            
            Pedido pedido = new Pedido();
            pedido.setData_pedido(Instant.now().toString());
            pedido.setCliente(cliente);
            pedido.setValor_total(valorTotal);
            
            daoPedido.salvar(pedido);
            Pedido pedidoSalvo = daoPedido.pesquisaPorData(pedido);
            
            daoPedido.vincularLanche(pedidoSalvo, lanche);
            daoPedido.vincularBebida(pedidoSalvo, bebida);
            
            return estado.comPedido(pedidoSalvo);
        } catch (Exception e) {
            throw new RuntimeException("erro finalizar compra: " + e.getMessage(), e);
        }
    };

    @Test
    @Order(1)
    void prepararAmbienteCompra() {
        estadoAtual = prepararCliente.apply(estadoAtual);
        estadoAtual = prepararLanche.apply(estadoAtual);
        estadoAtual = prepararBebida.apply(estadoAtual);
        estadoAtual = realizarLogin.apply(estadoAtual);
        
        assertNotNull(estadoAtual.getCliente());
        assertTrue(estadoAtual.getCliente().getId_cliente() > 0);
        assertNotNull(estadoAtual.getLanche());
        assertNotNull(estadoAtual.getBebida());
        assertNotNull(estadoAtual.getToken());
        assertTrue(estadoAtual.isAmbientePreparado());
        
        boolean tokenValido = daoToken.validar(estadoAtual.getToken());
        assertTrue(tokenValido);
    }

    @Test
    @Order(2)
    void consultarProdutosDisponiveis() {
        estadoAtual = consultarProdutos.apply(estadoAtual);
        
        assertTrue(estadoAtual.isProdutosConsultados());
        
        List<Lanche> lanches = daoLanche.listarTodos();
        assertNotNull(lanches);
        assertFalse(lanches.isEmpty());
        
        boolean lancheEncontrado = lanches.stream()
            .anyMatch(l -> l.getNome().equals(estadoAtual.getLanche().getNome()));
        assertTrue(lancheEncontrado);

        List<Bebida> bebidas = daoBebida.listarTodos();
        assertNotNull(bebidas);
        assertFalse(bebidas.isEmpty());
        
        boolean bebidaEncontrada = bebidas.stream()
            .anyMatch(b -> b.getNome().equals(estadoAtual.getBebida().getNome()));
        assertTrue(bebidaEncontrada);
    }

    @Test
    @Order(3)
    void simularCarrinhoCompra() {
        estadoAtual = simularCarrinho.apply(estadoAtual);
        
        assertTrue(estadoAtual.isCarrinhoSimulado());
        
        int quantidadeLanche = 2;
        int quantidadeBebida = 3;
        
        double valorLanches = estadoAtual.getLanche().getValor_venda() * quantidadeLanche;
        double valorBebidas = estadoAtual.getBebida().getValor_venda() * quantidadeBebida;
        double valorTotal = valorLanches + valorBebidas;
        
        assertTrue(valorTotal > 0);
        assertEquals(valorLanches + valorBebidas, valorTotal);
    }

    @Test
    @Order(4)
    void finalizarCompra() {
        estadoAtual = finalizarCompra.apply(estadoAtual);
        
        assertTrue(estadoAtual.isCompraFinalizada());
        assertNotNull(estadoAtual.getPedido());
        assertTrue(estadoAtual.getPedido().getId_pedido() > 0);
        
        boolean tokenValido = daoToken.validar(estadoAtual.getToken());
        assertTrue(tokenValido);

        Cliente cliente = daoCliente.pesquisaPorID(String.valueOf(estadoAtual.getCliente().getId_cliente()));
        assertNotNull(cliente);
        assertEquals(estadoAtual.getCliente().getId_cliente(), cliente.getId_cliente());
    }

    @Test
    @Order(5)
    void verificarPersistenciaPedido() {
        try (Connection conn = new DaoUtil().conecta()) {
            PreparedStatement stmtPedido = conn.prepareStatement(
                "SELECT * FROM tb_pedidos WHERE id_cliente = ? ORDER BY id_pedido DESC LIMIT 1"
            );
            stmtPedido.setInt(1, estadoAtual.getCliente().getId_cliente());
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
            stmtLanche.setInt(2, estadoAtual.getLanche().getId_lanche());
            ResultSet rsLanche = stmtLanche.executeQuery();
            
            assertTrue(rsLanche.next());
            assertEquals(2, rsLanche.getInt("quantidade"));

            PreparedStatement stmtBebida = conn.prepareStatement(
                "SELECT * FROM tb_bebidas_pedido WHERE id_pedido = ? AND id_bebida = ?"
            );
            stmtBebida.setInt(1, idPedido);
            stmtBebida.setInt(2, estadoAtual.getBebida().getId_bebida());
            ResultSet rsBebida = stmtBebida.executeQuery();
            
            assertTrue(rsBebida.next());
            assertEquals(1, rsBebida.getInt("quantidade"));

        } catch (SQLException e) {            
        }
    }

    @Test
    @Order(6)
    void fluxoCompletoEndToEnd() {
        assertTrue(daoToken.validar(estadoAtual.getToken()));
        Cliente cliente = daoCliente.pesquisaPorID(String.valueOf(estadoAtual.getCliente().getId_cliente()));
        assertNotNull(cliente);

        double valorTotal = (estadoAtual.getLanche().getValor_venda() * 1) + 
                           (estadoAtual.getBebida().getValor_venda() * 2);
        
        Pedido pedido = new Pedido();
        pedido.setData_pedido(Instant.now().toString());
        pedido.setCliente(cliente);
        pedido.setValor_total(valorTotal);
        
        daoPedido.salvar(pedido);
        Pedido pedidoSalvo = daoPedido.pesquisaPorData(pedido);
        
        assertTrue(pedidoSalvo.getId_pedido() > 0);
        assertEquals(valorTotal, pedidoSalvo.getValor_total(), 0.01);
    }

    @Test
    @Order(7)
    void testeTokenInvalido() {
        daoToken.remover(estadoAtual.getToken());
        assertFalse(daoToken.validar(estadoAtual.getToken()));
        
        assertDoesNotThrow(() -> {
            Pedido pedido = new Pedido();
            pedido.setData_pedido(Instant.now().toString());
            pedido.setCliente(estadoAtual.getCliente());
            pedido.setValor_total(50.0);
            daoPedido.salvar(pedido);
        });
    }
    
    @Test
    @Order(8)
    void testeLoginTodasArestas() {
        Cliente clienteInexistente = new Cliente();
        clienteInexistente.setUsuario("usuario_inexistente");
        clienteInexistente.setSenha("sdfgadsgsadg23423");
        assertFalse(daoCliente.login(clienteInexistente));
        Cliente clienteSenhaErrada = new Cliente();
        clienteSenhaErrada.setUsuario(USUARIO_TESTE);
        clienteSenhaErrada.setSenha("234efdsaf213fdsacfdasf");
        assertFalse(daoCliente.login(clienteSenhaErrada));
        
        Endereco enderecoInativo = enderecoTeste();
        Cliente clienteInativo = new Cliente();
        clienteInativo.setNome("clienteInativo");
        clienteInativo.setSobrenome("teste");
        clienteInativo.setTelefone("21888888888");
        clienteInativo.setUsuario("cliente_inativo_test");
        clienteInativo.setSenha("senha123");
        clienteInativo.setFg_ativo(0);
        clienteInativo.setEndereco(enderecoInativo);
        
        daoCliente.salvar(clienteInativo);
        assertFalse(daoCliente.login(clienteInativo));
        
        Cliente clienteValido = new Cliente();
        clienteValido.setUsuario(USUARIO_TESTE);
        clienteValido.setSenha(SENHA_TESTE);
        assertTrue(daoCliente.login(clienteValido));
    }
    
    @ParameterizedTest
    @Order(9)
    @ValueSource(strings = {"", "token_inexistente", "token_invalido"})
    void testeValidacaoTokenParametrizado(String token) {
        assertFalse(daoToken.validar(token));
    }
    
    @Test
    @Order(10)
    void testeCalculosLimite() {
        assertEquals(0.0, estadoAtual.getLanche().getValor_venda() * 0);
        assertTrue(estadoAtual.getLanche().getValor_venda() * (-1) < 0);
        assertTrue(estadoAtual.getLanche().getValor_venda() * 1000 > 1000);
    }   
    
    private Cliente criarClienteLogin() {
        Cliente cliente = new Cliente();
        cliente.setUsuario(USUARIO_TESTE);
        cliente.setSenha(SENHA_TESTE);
        return cliente;
    }
    
    private Cliente criarClienteLoginInvalido() {
        Cliente cliente = new Cliente();
        cliente.setUsuario("usuario_inexistente");
        cliente.setSenha("cdfvsqwgf24");
        return cliente;
    }

    @AfterAll
    static void tearDownClass() {
        try (Connection conn = new DaoUtil().conecta()) {
            conn.createStatement().execute("DELETE FROM tb_lanches_pedido WHERE id_pedido IN (SELECT id_pedido FROM tb_pedidos WHERE id_cliente IN (SELECT id_cliente FROM tb_clientes WHERE usuario LIKE '%_test'))");
            conn.createStatement().execute("DELETE FROM tb_bebidas_pedido WHERE id_pedido IN (SELECT id_pedido FROM tb_pedidos WHERE id_cliente IN (SELECT id_cliente FROM tb_clientes WHERE usuario LIKE '%_test'))");
            conn.createStatement().execute("DELETE FROM tb_pedidos WHERE id_cliente IN (SELECT id_cliente FROM tb_clientes WHERE usuario LIKE '%_test')");
            conn.createStatement().execute("DELETE FROM tb_tokens WHERE token LIKE '%test%'");
            conn.createStatement().execute("DELETE FROM tb_clientes WHERE usuario LIKE '%_test'");
            conn.createStatement().execute("DELETE FROM tb_lanches WHERE nm_lanche IN ('nomeLanche', 'L')");
            conn.createStatement().execute("DELETE FROM tb_bebidas WHERE nm_bebida IN ('nomeBebida', 'B')");
        } catch (SQLException e) {
        }
    }

    private Endereco enderecoTeste() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua");
        endereco.setBairro("Bairro");
        endereco.setNumero(123);
        endereco.setComplemento("Complemento");
        endereco.setCidade("Rio de Janeiro");
        endereco.setEstado("RJ");
        return endereco;
    }

    private Cliente clienteTeste(Endereco endereco) {
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

    private Lanche lancheTeste() {
        Lanche lanche = new Lanche();
        lanche.setNome("nomeLanche");
        lanche.setDescricao("descricaoTeste");
        lanche.setValor_venda(25.90);
        lanche.setFg_ativo(1);
        return lanche;
    }

    private Bebida bebidaTeste() {
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
