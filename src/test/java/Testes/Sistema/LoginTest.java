package Testes.Sistema;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        try {
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-web-security");
            options.addArguments("--disable-features=VizDisplayCompositor");
            
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
        } catch (Exception e) {
            System.err.println("erro webdriver: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginFuncionarioCompleto() {
        try {
            driver.get("http://localhost:8080");

            driver.get("http://localhost:8080/view/login/login.html");
            Thread.sleep(2000);
            
            String tituloPagina = driver.getTitle();
            assertTrue(tituloPagina.contains("Login"));

            WebElement titulo = null;
            try {
                titulo = driver.findElement(By.className("titlePage"));
            } catch (NoSuchElementException e1) {
                try {
                    titulo = driver.findElement(By.tagName("h1"));
                } catch (NoSuchElementException e2) {
                    try {
                        titulo = driver.findElement(By.xpath("//*[contains(text(),'Login')]"));
                    } catch (NoSuchElementException e3) {
                        System.out.println("title nao encontrado");
                    }
                }
            }          
            
            WebElement linkFuncionario = null;
            try {
                linkFuncionario = driver.findElement(By.xpath("//a[contains(@href, 'login_Funcionario.html')]"));
            } catch (NoSuchElementException e1) {
                try {
                    linkFuncionario = driver.findElement(By.xpath("//a[contains(text(), 'Funcionário') or contains(text(), 'funcionário') or contains(text(), 'Administrativo')]"));
                } catch (NoSuchElementException e2) {
                    java.util.List<WebElement> links = driver.findElements(By.tagName("a"));
                    for (int i = 0; i < links.size(); i++) {
                        WebElement link = links.get(i);
                        System.out.println("  Link[" + i + "]: href=" + link.getAttribute("href") + 
                                         ", texto='" + link.getText() + "'");
                    }
                }
            }
            
            if (linkFuncionario != null) {
                linkFuncionario.click();
            }
            
            Thread.sleep(2000);

            String urlAtual = driver.getCurrentUrl();
            assertTrue(urlAtual.contains("login_Funcionario.html"));
            WebElement campoUsuario = driver.findElement(By.id("loginInput"));
            WebElement campoSenha = driver.findElement(By.id("senhaInput"));
            WebElement botaoSubmit = driver.findElement(By.className("buttonSubmit"));

            assertTrue(campoUsuario.isDisplayed());
            assertTrue(campoSenha.isDisplayed());
            assertTrue(botaoSubmit.isDisplayed());

            campoUsuario.clear();
            campoUsuario.sendKeys("admin");
            
            campoSenha.clear();
            campoSenha.sendKeys("admin");
            
            Thread.sleep(1000);

            assertEquals("admin", campoUsuario.getAttribute("value"));
            assertEquals("admin", campoSenha.getAttribute("value"));
            assertEquals("Entrar", botaoSubmit.getText());
            botaoSubmit.click();
            
            Thread.sleep(3000);
            String urlFinal = driver.getCurrentUrl();
            assertNotEquals("http://localhost:8080/view/login/login_Funcionario.html", 
                           urlFinal);
            
            assertFalse(urlFinal.contains("login"));
            String pageSource = driver.getPageSource();
            assertFalse(pageSource.isEmpty());

        } catch (Exception e) {
            System.err.println("erro no teste");
    }    
}
}
