package Testes.Sistema;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;



public class ConfereIngredientesTest {

    

    WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();  

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // para rodar sem interface gráfica
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

    //Teste para avaliar se os ingredientes dos lanches estão retornando normalmente em tela junto ao preço.
    @Test
    public void testDescricaoEPrecoDosLanches() throws InterruptedException {

        assertNotNull(driver, "Driver não foi inicializado!");

        // Acessa a página dos Lanches 
        driver.get("http://localhost:8080/view/menu/menu.html");
        Thread.sleep(1000);

        // Clica em Lanches
        driver.findElement(By.xpath("//div[@class='opcoes']/a[contains(text(),'Lanches')]")).click();
        Thread.sleep(1000);

        // Cria uma listinha com todos os lanches para ver a descrição
        List<WebElement> lanches = driver.findElements(By.className("divLanche"));

        // Dentro do lanche, clica no botão "Veja os Ingredientes"
        for (WebElement lanche : lanches) {
            List<WebElement> botoes = lanche.findElements(By.className("botaoLanche"));
            for (WebElement botao : botoes) {
                if (botao.getText().contains("Veja os Ingredientes")) {
                    botao.click();
                    Thread.sleep(1000);
                    break;
                }
            }

            // Verifica se o texto de descrição existe
            WebElement descricao = lanche.findElement(By.className("textoDescricao"));
            String textoDescricao = descricao.getText().trim();

            if (textoDescricao.isBlank()) {
                System.out.println("Descrição vazia, lanche ignorado.");
                continue; // ignora este lanche
            }

            // Verifica se o texto possui pelo menos 1 item com traço (formato "-Item")
            System.out.println(descricao);
            System.out.println(textoDescricao);
            System.out.println("Descrição analisada: [" + textoDescricao + "]");
            assertTrue(textoDescricao.contains("-"), "Descrição não possui item com '-': " + textoDescricao);

            // Verifica se o preço existe
            WebElement preco = lanche.findElement(By.className("preco"));
            String textoPreco = preco.getText().trim();

            assertTrue(textoPreco.contains("R$"), "Item da lista não tem preço 'R$' não localizado: " + textoPreco);

            System.out.println("Lanche OK: " + textoDescricao + " | Preço: " + textoPreco);
        }
    }
}