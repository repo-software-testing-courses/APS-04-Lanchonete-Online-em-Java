package Testes.Sistema;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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

    @Test
    void deveCadastrarBebida() throws InterruptedException {
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

        // Gambiarra pra esperar 2 segundos pra ver o que acontece
        Thread.sleep(2000);

        WebElement form = driver.findElement(By.id("CadBebidas"));
        boolean estaVisivel = form.isDisplayed();

        // Se não tiver mais visível, o cadastro deu certo
        assert !estaVisivel : "Erro: o formulário ainda está visível — talvez não salvou.";
    }
}
