package Controllers;

import DAO.DaoIngrediente;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class getIngredientesPorLancheCliente extends HttpServlet {

    private DaoIngrediente daoIngrediente;

    public getIngredientesPorLancheCliente() {
        this.daoIngrediente = new DaoIngrediente();
    }

    public getIngredientesPorLancheCliente(DaoIngrediente daoIngrediente) {
        this.daoIngrediente = daoIngrediente;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String IncomingJson = "";
        boolean resultado = true;

        if ((br != null) && resultado) {
            IncomingJson = br.readLine();
            byte[] bytes = IncomingJson.getBytes(ISO_8859_1);
            String jsonStr = new String(bytes, UTF_8);
            JSONObject dados = new JSONObject(jsonStr);

            List<Ingrediente> ingredientes = daoIngrediente.listarTodosPorLanche(dados.getInt("id"));

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
        return "getIngredientesPorLancheCliente Servlet";
    }
}
