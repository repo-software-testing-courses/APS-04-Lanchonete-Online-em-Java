package Controllers;

import DAO.DaoIngrediente;
import Helpers.ValidadorCookie;
import Model.Ingrediente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class removerIngrediente extends HttpServlet {

    private DaoIngrediente daoIngrediente;
    private ValidadorCookie validadorCookie;

    public removerIngrediente() {
        this.daoIngrediente = new DaoIngrediente();
        this.validadorCookie = new ValidadorCookie();
    }

    public removerIngrediente(DaoIngrediente daoIngrediente, ValidadorCookie validadorCookie) {
        this.daoIngrediente = daoIngrediente;
        this.validadorCookie = validadorCookie;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        boolean resultado = false;

        try {
            Cookie[] cookies = request.getCookies();
            resultado = validadorCookie.validarFuncionario(cookies);
        } catch (NullPointerException e) {
        }

        if ((br != null) && resultado) {
            json = br.readLine();
            byte[] bytes = json.getBytes(ISO_8859_1);
            String jsonStr = new String(bytes, UTF_8);
            JSONObject dados = new JSONObject(jsonStr);

            Ingrediente ingrediente = new Ingrediente();
            ingrediente.setId_ingrediente(dados.getInt("id"));
            ingrediente.setNome(dados.getString("nome"));
            ingrediente.setDescricao(dados.getString("descricao"));
            ingrediente.setQuantidade(dados.getInt("quantidade"));
            ingrediente.setValor_compra(dados.getDouble("ValorCompra"));
            ingrediente.setValor_venda(dados.getDouble("ValorVenda"));
            ingrediente.setTipo(dados.getString("tipo"));
            ingrediente.setFg_ativo(1);

            daoIngrediente.remover(ingrediente);

            try (PrintWriter out = response.getWriter()) {
                out.println("Ingrediente Alterado!");
            }
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("erro");
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "removerIngrediente Servlet";
    }
}
