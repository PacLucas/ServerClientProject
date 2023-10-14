package com.servidor.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseManager {
    private static Connection connection;

    public DatabaseManager(Connection connection) {
        DatabaseManager.connection = connection;
    }

    public boolean inserirUsuario(String nome, String email, String senha, String tipo) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            String hashedPassword = BCrypt.hashpw(senha, BCrypt.gensalt());
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword);
            preparedStatement.setString(4, tipo);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailJaExiste(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Se count for maior que 0, o email já existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet verificarCredenciais(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPasswordBCrypt = resultSet.getString("senha");

                if (BCrypt.checkpw(senha, storedPasswordBCrypt)) {
                    return resultSet;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}