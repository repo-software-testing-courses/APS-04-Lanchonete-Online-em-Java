package Testes.Unitários;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import DAO.DaoBebida;


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
    }

