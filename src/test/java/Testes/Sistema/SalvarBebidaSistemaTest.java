package Testes.Sistema;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.time.Duration;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;



public class SalvarBebidaSistemaTest {

    

    WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();  

        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless=new"); // para rodar sem interface gráfica
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

        // Método de login reutilizável dentro da própria classe
    private void loginComoFuncionario() throws InterruptedException {
        driver.get("http://localhost:8080/view/login/login.html");
        Thread.sleep(1000);

        // Verifica se está na tela de login
        String tituloPagina = driver.getTitle();
        if (!tituloPagina.contains("Login")) {
            throw new IllegalStateException("Não está na tela de login.");
        }

        // Clica no link de login de funcionário
        try {
            WebElement linkFuncionario = driver.findElement(By.xpath("//a[contains(@href, 'login_Funcionario.html')]"));
            linkFuncionario.click();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Link para login de funcionário não encontrado.");
        }

        Thread.sleep(1000);

        // Esperar o campo de e-mail estar visível
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement campoEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginInput")));
        WebElement campoSenha = driver.findElement(By.id("senhaInput"));

        // Preencher os campos
        campoEmail.sendKeys("admin");
        campoSenha.sendKeys("admin");

        // Simula o ENTER no campo de senha para submeter o formulário
        WebElement botaoEntrar = driver.findElement(By.xpath("//button[contains(text(), 'Entrar')]"));
        botaoEntrar.click();

        Thread.sleep(1000);
    }

    @Test
    void deveCadastrarBebida() throws InterruptedException {
        // Fazer login antes de tentar cadastrar a bebida
        loginComoFuncionario();
        //Thread.sleep(6000);
        driver.get("http://localhost:8080/view/painel/painel.html");


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement botaoCadastrar = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Cadastrar Bebidas')]"))
        );
        botaoCadastrar.click();



        Thread.sleep(6000);
        // Aguarda o input 'nome' estar visível e envia o texto
        WebElement campoNome = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//form[@id='addBebida']//input[@name='nome']")));
        campoNome.sendKeys("Guaraná Teste");

        // Select para o tipo
        WebElement campoTipo = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//form[@id='addBebida']//select[@name='tipo']")));
        Select selectTipo = new Select(campoTipo);
        selectTipo.selectByValue("refrigerante");

        // Outros campos
        driver.findElement(By.xpath("//form[@id='addBebida']//input[@name='quantidade']")).sendKeys("15");
        driver.findElement(By.xpath("//form[@id='addBebida']//input[@name='ValorCompra']")).sendKeys("4.00");
        driver.findElement(By.xpath("//form[@id='addBebida']//input[@name='ValorVenda']")).sendKeys("6.00");
        driver.findElement(By.xpath("//form[@id='addBebida']//textarea[@name='descricao']")).sendKeys("Guaraná gelado de teste");

        // Botão salvar
        WebElement botaoSalvar = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//form[@id='addBebida']//input[@type='button' and @name='salvar']")));
        botaoSalvar.click();

    }


}