/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class DaoUtil {
    
    private static String getHost() {
        // Detecta se está rodando dentro do container Docker ou no host
        String dockerHost = System.getenv("DB_HOST");
        if (dockerHost != null) {
            return dockerHost;
        }
        
        // Verifica se está no container (onde 'db' resolve)
        try {
            java.net.InetAddress.getByName("db");
            return "db";
        } catch (java.net.UnknownHostException e) {
            // Está no host local
            return "localhost";
        }
    }
    
    private static String getPort() {
        // Se está no container, usa porta padrão
        if ("db".equals(getHost())) {
            return "5432";
        }
        // Se está no host local, usa porta mapeada
        return "5433";
    }
    
    public Connection conecta() {
        String host = getHost();
        String port = getPort();
        String url = "jdbc:postgresql://" + host + ":" + port + "/lanchonete";
        String user = "postgres";
        String password = "123456";
        
        try {
            Connection conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
