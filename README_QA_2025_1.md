# Qualidade e Teste de Software 2025.1

# Links
ISO 25010: https://docs.google.com/document/d/150YBd9KHCjxVCDo0uLmm2s0dKvBKr9marKX2-KIz8o0/edit?tab=t.0

Apresentação: www.canva.com/design/DAGtFCWyR3o/lcjF80iUKg-F6QW3eEDBXg/edit?utm_content=DAGtFCWyR3o&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

Repositório Original do grupo: https://github.com/DanFP00/APS-04-Lanchonete-Online-em-Java


#Observações

- A estrutura de pastas dos testes, foi pessoal de cada integrante do grupo, segue onde achar todos os testes:

test
└── java
    ├── DAO
    │   ├── TestesIngredientes
    │   │   ├── IngredienteIntegracaoTest.java
    │   │   ├── IngredientesMockitoTest.java
    │   ├── ClienteControllerTest.java
    │   ├── DaoClienteTest.java
    │
    ├── Helpers
    │   └── ValidadorCookieTest.java
    │
    ├── Integracao
    │   ├── CadastroIntegrationTest.java
    │   ├── DatabaseConnectionTest.java
    │   ├── FluxoCompraIntegrationTest.java
    │   └── LoginIntegrationTest.java
    │
    ├── integration
    │   └── FluxoCompraIntegrationTest.java
    │
    ├── Testes
    │   ├── Integração
    │   │   ├── IngredienteBdTest.java
    │   │   └── LogoutServletTest.java
    │
    ├── Sistema
    │   ├── CadastroClienteTest.java
    │   ├── CadastroIngredienteTest.java
    │   ├── LoginTest.java
    │   └── SalvarBebidaSistemaTest.java
    │
    └── Unitários
        ├── EncryptadorMD5Test.java
        └── IngredientesMockitoTest.java



- Fotos das analises do Pitest estõa na apresentação


Foi feita uma pequena alteração numa classe para lançar exceção ao invés de Null em caso de erro. 
Essa classe se encontra em: src\java\Helpers\EncryptadorMD5_to_be.java