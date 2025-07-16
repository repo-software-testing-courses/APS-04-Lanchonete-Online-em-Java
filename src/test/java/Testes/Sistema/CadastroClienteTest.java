package Testes.Sistema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CadastroClienteTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }



    @Test
    public void CadastroCliente() throws InterruptedException {
        // Abre a página de Login
        driver.get("http://localhost:8080/view/login/login.html");

        // Espera a página carregar
        Thread.sleep(1000);

        assertEquals("http://localhost:8080/view/login/login.html", driver.getCurrentUrl());


        // Clica no botão de Criar conta
        driver.findElement(By.className("linkNewCreate")).click();
        Thread.sleep(1000);


        assertEquals("http://localhost:8080/view/cadastro/cadastro.html", driver.getCurrentUrl());

        // Escreve "Nome Teste" no campo Nome
        driver.findElement(By.name("nome")).sendKeys("Nome Teste");
        Thread.sleep(500);

        // Escreve "Sobrenome" no campo Sobrenome
        driver.findElement(By.name("sobrenome")).sendKeys("Sobrenome");
        Thread.sleep(500);

        // Escreve "123456789" no campo Telefone
        driver.findElement(By.name("telefone")).sendKeys("123456789");
        Thread.sleep(500);

        // Escreve "testeNome" no campo Usuario
        driver.findElement(By.name("usuario")).sendKeys("testeNome");
        Thread.sleep(500);

        // Escreve "12345" no campo Senha
        driver.findElement(By.name("senha")).sendKeys("12345");
        Thread.sleep(500);

        //Campo de Endereco


        // Escreve "110" no campo numero
        driver.findElement(By.name("rua")).sendKeys("Doutor March");
        Thread.sleep(500);

        // Escreve "110" no campo numero
        driver.findElement(By.name("numero")).sendKeys("110");
        Thread.sleep(500);

        // Escreve "Barreto" no campo bairro
        driver.findElement(By.name("bairro")).sendKeys("Barreto");
        Thread.sleep(500);

        // Escreve "Condominio Teste" no campo complemento
        driver.findElement(By.name("complemento")).sendKeys("Condominio Teste");
        Thread.sleep(500);

        // Escreve "testeNome" no campo cidade
        driver.findElement(By.name("cidade")).sendKeys("Niterói");
        Thread.sleep(500);

        // Escreve "RJ" no campo estado
        WebElement dropdownEstado = driver.findElement(By.name("estado"));

        // Crie um objeto Select
        Select selectEstado = new Select(dropdownEstado);

        // Selecione "RJ" por:
        // 1. Texto visível (recomendado se você sabe o texto exato)
        selectEstado.selectByVisibleText("RJ");


        //Clica em cadastrar
        driver.findElement(By.className("buttonSubmit")).click();
        Thread.sleep(1500);

        // Interage com o alerta
        Alert alert = driver.switchTo().alert();
        String alertaTexto = alert.getText().trim(); // remove espaços e quebras de linha extras
        assertEquals("Usuário Cadastrado!", alertaTexto);
        alert.accept();


        assertEquals("http://localhost:8080/view/login/login.html", driver.getCurrentUrl());



        driver.findElement(By.id("loginInput")).sendKeys("testeNome");
        Thread.sleep(500);

        driver.findElement(By.id("senhaInput")).sendKeys("12345");
        Thread.sleep(500);


        //Clica em Entrar
        driver.findElement(By.className("buttonSubmit")).click();
        Thread.sleep(1500);


        assertEquals("http://localhost:8080/view/carrinho/carrinho.html", driver.getCurrentUrl());


    }

}
