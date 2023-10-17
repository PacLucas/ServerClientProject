package com.servidor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:ProjectDatabase.db";

    public static Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Conexão ao banco de dados estabelecida com sucesso.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return null;
        }
    }

}
