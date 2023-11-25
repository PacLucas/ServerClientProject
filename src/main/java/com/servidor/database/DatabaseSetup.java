package com.servidor.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void createTables(Connection connection) {
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS usuarios ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "email TEXT NOT NULL,"
                        + "nome TEXT NOT NULL,"
                        + "senha TEXT NOT NULL,"
                        + "tipo TEXT NOT NULL"
                        + ")";
                statement.execute(sql);
                System.out.println("Tabela 'usuarios' criada com sucesso.");
                sql = "CREATE TABLE IF NOT EXISTS pontos ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "nome TEXT NOT NULL,"
                        + "obs TEXT"
                        + ")";
                statement.execute(sql);
                System.out.println("Tabela 'pontos' criada com sucesso.");
                sql = "CREATE TABLE IF NOT EXISTS segmentos ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "ponto_origem INTEGER NOT NULL REFERENCES pontos(id),"
                        + "ponto_destino INTEGER NOT NULL REFERENCES pontos(id),"
                        + "direcao TEXT NOT NULL,"
                        + "distancia TEXT NOT NULL,"
                        + "obs TEXT"
                        + ")";
                statement.execute(sql);
                System.out.println("Tabela 'segmentos' criada com sucesso.");
            } catch (SQLException e) {
                System.err.println("Erro ao criar tabela: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Connection connection = DatabaseConnection.connect();
        createTables(connection);
    }
}
