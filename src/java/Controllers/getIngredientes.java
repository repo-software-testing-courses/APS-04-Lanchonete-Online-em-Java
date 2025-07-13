package Controllers;

import DAO.DaoIngrediente;
import Helpers.ValidadorCookie;
import Model.Ingrediente;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class getIngredientes extends HttpServlet {

    private DaoIngrediente daoIngrediente;
    private ValidadorCookie validadorCookie;

    public getIngredientes() {
        this.daoIngrediente = new DaoIngrediente();
        this.validadorCookie = new ValidadorCookie();
    }

    // Construtor para testes
    public getIngredientes(DaoIngrediente daoIngrediente, ValidadorCookie validadorCookie) {
        this.daoIngrediente = daoIngrediente;
        this.validadorCookie = validadorCookie;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        boolean resultado = false;
        try {
            Cookie[] cookies = request.getCookies();
            resultado = validadorCookie.validarFuncionario(cookies);
        } catch (NullPointerException e) {
            System.out.println(e);
        }

        if (resultado) {
            List<Ingrediente> ingredientes = daoIngrediente.listarTodos();

            Gson gson = new Gson();
            String json = gson.toJson(ingredientes);

            try (PrintWriter out = response.getWriter()) {
                out.print(json);
                out.flush();
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
        return "getIngredientes Servlet";
    }
}
