package Testes.Integração;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import DAO.DaoBebida;
import Model.Bebida;

class DaoBebidaTest {

    @Mock
    private Connection conexaoMock;

    @Mock
    private PreparedStatement stmtMock;

    @Mock
    private ResultSet rsMock;

    private DaoBebida daoBebida;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        daoBebida = new DaoBebida();

        Field field = DaoBebida.class.getDeclaredField("conecta");
        field.setAccessible(true);
        field.set(daoBebida, conexaoMock);
    }

    @Test
    void deveSalvarBebida() throws Exception {
        Bebida bebida = new Bebida();
        bebida.setNome("Coca");
        bebida.setDescricao("Refrigerante");
        bebida.setQuantidade(10);
        bebida.setValor_compra(2.5);
        bebida.setValor_venda(5.0);
        bebida.setTipo("Lata");
        bebida.setFg_ativo(1);

        when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);

        assertDoesNotThrow(() -> daoBebida.salvar(bebida));

        verify(stmtMock).setString(1, "Coca");
        verify(stmtMock).setString(2, "Refrigerante");
        verify(stmtMock).setInt(3, 10);
        verify(stmtMock).setDouble(4, 2.5);
        verify(stmtMock).setDouble(5, 5.0);
        verify(stmtMock).setString(6, "Lata");
        verify(stmtMock).setInt(7, 1);
        verify(stmtMock).execute();
        verify(stmtMock).close();
    }

    @Test
    void deveListarTodasBebidas() throws Exception {
        when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
        when(stmtMock.executeQuery()).thenReturn(rsMock);

        when(rsMock.next()).thenReturn(true, false);
        when(rsMock.getInt("id_bebida")).thenReturn(1);
        when(rsMock.getString("nm_bebida")).thenReturn("Coca");
        when(rsMock.getString("descricao")).thenReturn("Refrigerante");
        when(rsMock.getInt("quantidade")).thenReturn(10);
        when(rsMock.getDouble("valor_compra")).thenReturn(2.5);
        when(rsMock.getDouble("valor_venda")).thenReturn(5.0);
        when(rsMock.getString("tipo")).thenReturn("Lata");

        List<Bebida> bebidas = daoBebida.listarTodos();

        assertEquals(1, bebidas.size());
        assertEquals("Coca", bebidas.get(0).getNome());

        verify(rsMock).close();
        verify(stmtMock).close();
    }

    @Test
    void deveAlterarBebida() throws Exception {
        Bebida bebida = new Bebida();
        bebida.setId_bebida(1);
        bebida.setNome("Pepsi");
        bebida.setDescricao("Refrigerante");
        bebida.setQuantidade(20);
        bebida.setValor_compra(3.0);
        bebida.setValor_venda(6.0);
        bebida.setTipo("Garrafa");

        when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);

        assertDoesNotThrow(() -> daoBebida.alterar(bebida));

        verify(stmtMock).setString(1, "Pepsi");
        verify(stmtMock).setString(2, "Refrigerante");
        verify(stmtMock).setInt(3, 20);
        verify(stmtMock).setDouble(4, 3.0);
        verify(stmtMock).setDouble(5, 6.0);
        verify(stmtMock).setString(6, "Garrafa");
        verify(stmtMock).setInt(7, 1);
        verify(stmtMock).execute();
        verify(stmtMock).close();
    }

    @Test
    void deveRemoverBebida() throws Exception {
        Bebida bebida = new Bebida();
        bebida.setId_bebida(1);

        when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);

        assertDoesNotThrow(() -> daoBebida.remover(bebida));

        verify(stmtMock).setInt(1, 1);
        verify(stmtMock).execute();
        verify(stmtMock).close();
    }

    @Test
    void devePesquisarPorNome() throws Exception {
        when(conexaoMock.prepareStatement(anyString())).thenReturn(stmtMock);
        when(stmtMock.executeQuery()).thenReturn(rsMock);

        when(rsMock.next()).thenReturn(true);
        when(rsMock.getInt("id_bebida")).thenReturn(1);
        when(rsMock.getString("nm_bebida")).thenReturn("Guaraná");
        when(rsMock.getString("descricao")).thenReturn("Refrigerante");
        when(rsMock.getInt("quantidade")).thenReturn(15);
        when(rsMock.getDouble("valor_compra")).thenReturn(1.5);
        when(rsMock.getDouble("valor_venda")).thenReturn(3.0);
        when(rsMock.getString("tipo")).thenReturn("Lata");

        Bebida bebida = daoBebida.pesquisaPorNome("Guaraná");

        assertEquals("Guaraná", bebida.getNome());
        assertEquals(1, bebida.getId_bebida());

        verify(rsMock).close();
        verify(stmtMock).close();
    }
}
