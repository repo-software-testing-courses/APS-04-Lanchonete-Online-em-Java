package DAO.TestesIngredientes;

import DAO.DaoIngrediente;
import Model.Ingrediente;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IngredienteIntegracaoTest {

    private static DaoIngrediente dao;

    @BeforeAll
    static void setup() {
        dao = new DaoIngrediente(); // Inicializa DAO com conexão real
    }

    @Test
    @Order(1)
    void testSalvarEBuscarIngrediente() {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNome("TestTomate");
        ingrediente.setDescricao("Vermelho");
        ingrediente.setQuantidade(50);
        ingrediente.setValor_compra(1.0);
        ingrediente.setValor_venda(2.0);
        ingrediente.setTipo("legume");
        ingrediente.setFg_ativo(1);

        dao.salvar(ingrediente); // Insere no banco

        Ingrediente resultado = dao.pesquisaPorNome(ingrediente); // Busca pelo nome

        assertNotNull(resultado);
        assertEquals("TestTomate", resultado.getNome());
        assertEquals("Vermelho", resultado.getDescricao());
    }

    @Test
    @Order(2)
    void testListarTodos() {
        List<Ingrediente> lista = dao.listarTodos(); // Lista todos ingredientes ativos

        assertNotNull(lista);
        assertTrue(lista.size() >= 1); // Espera que pelo menos 1 esteja presente
    }

    @Test
    @Order(3)
    void testAlterarIngrediente() {
        Ingrediente ingrediente = dao.pesquisaPorNome(new Ingrediente() {{
            setNome("TestTomate");
        }});
        assertNotNull(ingrediente);

        ingrediente.setDescricao("Vermelho Escuro");
        ingrediente.setQuantidade(99);
        dao.alterar(ingrediente); // Altera dados do ingrediente

        Ingrediente alterado = dao.pesquisaPorNome(ingrediente);
        assertEquals("Vermelho Escuro", alterado.getDescricao());
        assertEquals(99, alterado.getQuantidade());
    }

    @Test
    @Order(4)
    void testRemoverIngrediente() {
        Ingrediente ingrediente = dao.pesquisaPorNome(new Ingrediente() {{
            setNome("TestTomate");
        }});
        assertNotNull(ingrediente);

        dao.remover(ingrediente); // Remove do banco

        Ingrediente buscado = dao.pesquisaPorNome(ingrediente);
        assertNull(buscado.getNome()); // Espera que não encontre mais
    }

    @Test
    @Order(5)
    void testSalvarIngredienteComNomeDuplicado() {
        // Salva primeiro ingrediente
        Ingrediente ingrediente1 = new Ingrediente();
        ingrediente1.setNome("Duplicado");
        ingrediente1.setDescricao("Primeiro");
        ingrediente1.setQuantidade(10);
        ingrediente1.setValor_compra(1.5);
        ingrediente1.setValor_venda(2.5);
        ingrediente1.setTipo("teste");
        ingrediente1.setFg_ativo(1);
        dao.salvar(ingrediente1);

        // Salva outro com o mesmo nome
        Ingrediente ingrediente2 = new Ingrediente();
        ingrediente2.setNome("Duplicado");
        ingrediente2.setDescricao("Segundo");
        ingrediente2.setQuantidade(20);
        ingrediente2.setValor_compra(2.5);
        ingrediente2.setValor_venda(3.5);
        ingrediente2.setTipo("teste");
        ingrediente2.setFg_ativo(1);
        dao.salvar(ingrediente2);

        Ingrediente resultado = dao.pesquisaPorNome(ingrediente2);
        assertNotNull(resultado);
        assertEquals("Duplicado", resultado.getNome());

        // Limpa o registro duplicado após teste
        dao.remover(resultado);
    }

    @Test
    @Order(6)
    void testBuscarIngredienteInexistente() {
        Ingrediente inexistente = new Ingrediente();
        inexistente.setNome("IngredienteInexistente");
        Ingrediente resultado = dao.pesquisaPorNome(inexistente);

        // Espera que nome seja nulo para ingrediente não encontrado
        assertNull(resultado.getNome());
    }

    @Test
    @Order(7)
    void testListarTodosAposInsercao() {
        // Insere ingrediente temporário
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNome("ListarTeste");
        ingrediente.setDescricao("Teste Listagem");
        ingrediente.setQuantidade(5);
        ingrediente.setValor_compra(3.0);
        ingrediente.setValor_venda(4.0);
        ingrediente.setTipo("categoria");
        ingrediente.setFg_ativo(1);

        dao.salvar(ingrediente);

        List<Ingrediente> lista = dao.listarTodos();

        // Verifica se o ingrediente inserido está na lista
        boolean encontrado = lista.stream().anyMatch(i -> "ListarTeste".equals(i.getNome()));
        assertTrue(encontrado);

        // Limpa o registro após teste
        dao.remover(dao.pesquisaPorNome(ingrediente));
    }

    @Test
    @Order(8)
    void testAlterarIngredienteInexistente() {
        Ingrediente inexistente = new Ingrediente();
        inexistente.setId_ingrediente(99999); // ID que provavelmente não existe
        inexistente.setNome("Fake");
        inexistente.setDescricao("Não existe");
        inexistente.setQuantidade(0);
        inexistente.setValor_compra(0.0);
        inexistente.setValor_venda(0.0);
        inexistente.setTipo("teste");

        // Garante que não lança exceção ao tentar alterar
        assertDoesNotThrow(() -> dao.alterar(inexistente));

        Ingrediente resultado = dao.pesquisaPorNome(inexistente);
        assertNull(resultado.getNome());
    }

    @Test
    @Order(9)
    void testRemoverIngredienteInexistente() {
        Ingrediente fake = new Ingrediente();
        fake.setId_ingrediente(99999); // ID que não deve existir

        assertDoesNotThrow(() -> dao.remover(fake));
    }
}
