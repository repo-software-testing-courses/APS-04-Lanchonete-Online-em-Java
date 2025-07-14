package Testes.Sistema;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

class SalvarBebidaSistemaTest {

    private WebDriver driver;

    @BeforeEach
    void iniciar() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void fechar() {
        driver.quit();
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

        // Preencher o formulário de login
        driver.findElement(By.id("loginInput")).sendKeys("admin");
        driver.findElement(By.id("senhaInput")).sendKeys("admin");
        driver.findElement(By.xpath("//input[@type='submit']")).click();

        Thread.sleep(1000);
    }

    @Test
    void deveCadastrarBebida() throws InterruptedException {
        // Fazer login antes de tentar cadastrar a bebida
        loginComoFuncionario();

        driver.get("http://localhost:8080/view/painel/painel.html");

        WebElement botaoCadastrar = driver.findElement(By.xpath("//button[contains(text(),'Cadastrar Bebidas')]"));
        botaoCadastrar.click();

        driver.findElement(By.name("nome")).sendKeys("Guaraná Teste");
        driver.findElement(By.name("tipo")).sendKeys("refrigerante");
        driver.findElement(By.name("quantidade")).sendKeys("15");
        driver.findElement(By.name("ValorCompra")).sendKeys("4.00");
        driver.findElement(By.name("ValorVenda")).sendKeys("6.00");
        driver.findElement(By.name("descricao")).sendKeys("Guaraná gelado de teste");

        WebElement botaoSalvar = driver.findElement(By.xpath("//input[@value='Salvar' and @onclick='salvarBebida()']"));
        botaoSalvar.click();

        Thread.sleep(2000);

        WebElement form = driver.findElement(By.id("CadBebidas"));
        boolean estaVisivel = form.isDisplayed();

        assert !estaVisivel : "Erro: o formulário ainda está visível — talvez não salvou.";
    }
}
