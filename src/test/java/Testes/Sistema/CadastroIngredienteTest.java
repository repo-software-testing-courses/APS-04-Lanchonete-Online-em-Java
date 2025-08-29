package Testes.Sistema;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class CadastroIngredienteTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void abrirPaginaInicialEClicarNoPopup() throws InterruptedException {
        // Abre a página
        driver.get("http://localhost:8080/view/login/login.html");

        // Espera a página carregar
        Thread.sleep(1000);

        // Clica no botão de login para funcionários
        driver.findElement(By.xpath("/html/body/div/div/div/div[3]/a[2]")).click();
        Thread.sleep(1000);

        // Escreve "admin" no campo login
        driver.findElement(By.xpath("//*[@id=\"loginInput\"]")).sendKeys("admin");
        Thread.sleep(500);

        // Escreve "admin" no campo senha
        driver.findElement(By.xpath("//*[@id=\"senhaInput\"]")).sendKeys("admin");
        Thread.sleep(500);

        // Clica no botão de login
        driver.findElement(By.xpath("/html/body/div/div/div/div[3]/button")).click();
        Thread.sleep(1500); // tempo maior para esperar redirecionamento

        // Clica no botão desejado após o login
        driver.findElement(By.xpath("//*[@id=\"Agrupado\"]/div[3]/div[2]/div[2]/button[2]")).click();
        Thread.sleep(1000);

        // Escreve o nome do ingrediente
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/form/input[1]")).sendKeys("Queijo Parmesão");

        // Seleciona o tipo de produto (abre o select)
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/form/select")).click();
        Thread.sleep(500);

        // Escolhe a 4ª opção
        driver.findElement(By.xpath("//*[@id=\"addIngrediente\"]/select/option[4]")).click();
        Thread.sleep(500);

        // Preenche quantidade
        driver.findElement(By.xpath("//*[@id=\"addIngrediente\"]/input[2]")).sendKeys("100");

        // Preenche valor de compra
        driver.findElement(By.xpath("//*[@id=\"addIngrediente\"]/input[3]")).sendKeys("5.50");

        // Preenche valor de venda
        driver.findElement(By.xpath("//*[@id=\"addIngrediente\"]/input[4]")).sendKeys("8.00");

        // Escreve descrição
        driver.findElement(By.xpath("//*[@id=\"textArea1\"]")).sendKeys("Queijo parmesão ralado, usado em lanches especiais.");

        // Clica em "Salvar"
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/form/div/input[2]")).click();
        Thread.sleep(1500); // aguarda resposta/atualização

        // Interage com o alerta
        Alert alert = driver.switchTo().alert();
        String alertaTexto = alert.getText().trim(); // remove espaços e quebras de linha extras
        assertEquals("Ingrediente Salvo!", alertaTexto);
        alert.accept();

    }
}