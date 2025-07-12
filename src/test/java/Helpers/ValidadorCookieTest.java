package Helpers;

import DAO.DaoToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorCookieTest {

    @InjectMocks
    private ValidadorCookie validadorCookie;

    @Mock
    private DaoToken daoTokenMock;

    private Cookie[] cookies;

    @BeforeEach
    void setUp() {
        validadorCookie = new ValidadorCookie();
    }

    @Test
    void testValidar_ComTokenValido() {
        // Deve retornar true
        Cookie tokenCookie = new Cookie("token", "123-Gabriel");
        cookies = new Cookie[]{tokenCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class, 
                (mock, context) -> when(mock.validar("123-Gabriel")).thenReturn(true))) {
            
            boolean resultado = validadorCookie.validar(cookies);

            assertTrue(resultado);
        }
    }

    @Test
    void testValidar_ComTokenInvalido() {
        // Deve retornar false
        Cookie tokenCookie = new Cookie("token", "999-Daniel");
        cookies = new Cookie[]{tokenCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class, 
                (mock, context) -> when(mock.validar("999-Daniel")).thenReturn(false))) {
            
            boolean resultado = validadorCookie.validar(cookies);

            assertFalse(resultado);
        }
    }

    @Test
    void testValidar_SemTokenCookie() {
        // Deve retornar false
        Cookie outroCookie = new Cookie("session", "456-Deniel");
        cookies = new Cookie[]{outroCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class)) {
            
            boolean resultado = validadorCookie.validar(cookies);

            assertFalse(resultado);
        }
    }

    @Test
    void testValidar_CookiesVazio() {
        // Deve retornar false
        cookies = new Cookie[]{};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class)) {
            
            boolean resultado = validadorCookie.validar(cookies);

            assertFalse(resultado);
        }
    }

    @Test
    void testValidar_MultiplosCookies_ComToken() {
        // Deve retornar true
        Cookie cookie1 = new Cookie("session", "789-Guilherme");
        Cookie tokenCookie = new Cookie("token", "456-Gabriel");
        Cookie cookie3 = new Cookie("user", "321-Vania");
        cookies = new Cookie[]{cookie1, tokenCookie, cookie3};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class, 
                (mock, context) -> when(mock.validar("456-Gabriel")).thenReturn(true))) {
            
            boolean resultado = validadorCookie.validar(cookies);

            assertTrue(resultado);
        }
    }

    @Test
    void testValidarFuncionario_ComTokenFuncionarioValido() {
        // Deve retornar true
        Cookie tokenCookie = new Cookie("tokenFuncionario", "100-Lucas");
        cookies = new Cookie[]{tokenCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class, 
                (mock, context) -> when(mock.validar("100-Lucas")).thenReturn(true))) {
            
            boolean resultado = validadorCookie.validarFuncionario(cookies);

            assertTrue(resultado);
        }
    }

    @Test
    void testValidarFuncionario_ComTokenFuncionarioInvalido() {
        // Deve retornar false
        Cookie tokenCookie = new Cookie("tokenFuncionario", "200-Guilherme");
        cookies = new Cookie[]{tokenCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class, 
                (mock, context) -> when(mock.validar("200-Guilherme")).thenReturn(false))) {
            
            boolean resultado = validadorCookie.validarFuncionario(cookies);

            assertFalse(resultado);
        }
    }

    @Test
    void testValidarFuncionario_SemTokenFuncionarioCookie() {
        // Deve retornar false
        Cookie outroCookie = new Cookie("token", "300-Daniel");
        cookies = new Cookie[]{outroCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class)) {
            
            boolean resultado = validadorCookie.validarFuncionario(cookies);

            assertFalse(resultado);
        }
    }

    @Test
    void testDeletar_ComTokens() {
        // Deve chamar remover
        Cookie tokenCookie = new Cookie("token", "400-Vania");
        Cookie tokenFuncCookie = new Cookie("tokenFuncionario", "500-Lucas");
        Cookie outroCookie = new Cookie("session", "600-Gabriel");
        cookies = new Cookie[]{tokenCookie, tokenFuncCookie, outroCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class)) {
            
            validadorCookie.deletar(cookies);

            DaoToken daoTokenInstance = mockedConstruction.constructed().get(0);
            verify(daoTokenInstance).remover("400-Vania");
            verify(daoTokenInstance).remover("500-Lucas");
            verify(daoTokenInstance, never()).remover("600-Gabriel");
        }
    }

    @Test
    void testDeletar_SemTokens() {
        // Não deve chamar remover
        Cookie outroCookie = new Cookie("session", "700-Deniel");
        cookies = new Cookie[]{outroCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class)) {
            
            validadorCookie.deletar(cookies);

            DaoToken daoTokenInstance = mockedConstruction.constructed().get(0);
            verify(daoTokenInstance, never()).remover(anyString());
        }
    }

    @Test
    void testDeletar_ComExcecaoNoDAO() {
        // Deve lançar RuntimeException
        Cookie tokenCookie = new Cookie("token", "800-Guilherme");
        cookies = new Cookie[]{tokenCookie};

        try (MockedConstruction<DaoToken> mockedConstruction = mockConstruction(DaoToken.class, 
                (mock, context) -> doThrow(new RuntimeException("Erro no banco")).when(mock).remover("800-Guilherme"))) {
            
            assertThrows(RuntimeException.class, () -> validadorCookie.deletar(cookies));
        }
    }

    @Test
    void testGetCookieIdCliente_ComTokenValido() {
        // Deve retornar id
        Cookie tokenCookie = new Cookie("token", "123-Gabriel");
        cookies = new Cookie[]{tokenCookie};

        String resultado = validadorCookie.getCookieIdCliente(cookies);

        assertEquals("123", resultado);
    }

    @Test
    void testGetCookieIdCliente_SemToken() {
        // Deve retornar erro
        Cookie outroCookie = new Cookie("session", "900-Lucas");
        cookies = new Cookie[]{outroCookie};

        String resultado = validadorCookie.getCookieIdCliente(cookies);

        assertEquals("erro", resultado);
    }

    @Test
    void testGetCookieIdCliente_TokenSemHifen() {
        // Deve retornar token completo
        Cookie tokenCookie = new Cookie("token", "456Daniel");
        cookies = new Cookie[]{tokenCookie};

        String resultado = validadorCookie.getCookieIdCliente(cookies);

        assertEquals("456Daniel", resultado);
    }

    @Test
    void testGetCookieIdCliente_MultiplosCookies() {
        // Deve retornar id do token
        Cookie cookie1 = new Cookie("session", "111-Vania");
        Cookie tokenCookie = new Cookie("token", "789-Deniel");
        Cookie cookie3 = new Cookie("user", "222-Guilherme");
        cookies = new Cookie[]{cookie1, tokenCookie, cookie3};

        String resultado = validadorCookie.getCookieIdCliente(cookies);

        assertEquals("789", resultado);
    }

    @Test
    void testGetCookieIdFuncionario_ComTokenFuncionarioValido() {
        // Deve retornar id
        Cookie tokenCookie = new Cookie("tokenFuncionario", "456-Gabriel");
        cookies = new Cookie[]{tokenCookie};

        String resultado = validadorCookie.getCookieIdFuncionario(cookies);

        assertEquals("456", resultado);
    }

    @Test
    void testGetCookieIdFuncionario_SemTokenFuncionario() {
        // Deve retornar erro
        Cookie outroCookie = new Cookie("token", "333-Lucas");
        cookies = new Cookie[]{outroCookie};

        String resultado = validadorCookie.getCookieIdFuncionario(cookies);

        assertEquals("erro", resultado);
    }

    @Test
    void testGetCookieIdFuncionario_TokenFuncionarioSemHifen() {
        // Deve retornar token completo
        Cookie tokenCookie = new Cookie("tokenFuncionario", "789Vania");
        cookies = new Cookie[]{tokenCookie};

        String resultado = validadorCookie.getCookieIdFuncionario(cookies);

        assertEquals("789Vania", resultado);
    }

    @Test
    void testGetCookieIdFuncionario_MultiplosCookies() {
        // Deve retornar id do token funcionario
        Cookie cookie1 = new Cookie("session", "444-Guilherme");
        Cookie tokenCookie = new Cookie("token", "555-Daniel");
        Cookie tokenFuncCookie = new Cookie("tokenFuncionario", "999-Gabriel");
        cookies = new Cookie[]{cookie1, tokenCookie, tokenFuncCookie};

        String resultado = validadorCookie.getCookieIdFuncionario(cookies);

        assertEquals("999", resultado);
    }

    @Test
    void testGetCookieIdCliente_CookiesVazio() {
        // Deve retornar erro
        cookies = new Cookie[]{};

        String resultado = validadorCookie.getCookieIdCliente(cookies);

        assertEquals("erro", resultado);
    }

    @Test
    void testGetCookieIdFuncionario_CookiesVazio() {
        // Deve retornar erro
        cookies = new Cookie[]{};

        String resultado = validadorCookie.getCookieIdFuncionario(cookies);

        assertEquals("erro", resultado);
    }
}
