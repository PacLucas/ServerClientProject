package com.servidor.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static Connection connection;

    public DatabaseManager(Connection connection) {
        DatabaseManager.connection = connection;
    }

    public static boolean inserirUsuario(String nome, String email, String senha, String tipo) {
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

    public static boolean inserirPonto(String nome, String obs) {
        String sql = "INSERT INTO pontos (nome, obs) VALUES (?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, obs);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected);
            return rowsAffected > 0 ;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean inserirSegmento(Pontos pontoOrigem, Pontos pontoDestino, String direcao, String distancia, String obs) {
        String sql = "INSERT INTO segmentos (ponto_origem, ponto_destino, direcao, distancia, obs) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, pontoOrigem);
            preparedStatement.setObject(1, pontoDestino);
            preparedStatement.setString(2, direcao);
            preparedStatement.setString(2, distancia);
            preparedStatement.setString(2, obs);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected);
            return rowsAffected > 0 ;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean emailJaExiste(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
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

    public Boolean verificarUsuarioValido(String userId) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<User> listarUsuarios() {
        String sql = "SELECT id, nome, tipo, email FROM usuarios";
        List<User> users = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String tipo = resultSet.getString("tipo");
                String email = resultSet.getString("email");

                User user = new User(id, nome, tipo, email);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<Pontos> listarPontos() {
        String sql = "SELECT id, nome, obs FROM pontos";
        List<Pontos> pontos = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String obs = resultSet.getString("obs");

                Pontos ponto = new Pontos(id, nome, obs);
                pontos.add(ponto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pontos;
    }

    public List<Segmentos> listarSegmentos() {
        String sql = "SELECT id, ponto_origem, ponto_destino, direcao, distancia, obs FROM segmentos";
        List<Segmentos> segmentos = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Pontos pontoOrigem = resultSet.getObject("ponto_origem", Pontos.class);
                Pontos pontoDestino = resultSet.getObject("ponto_destino", Pontos.class);
                String direcao = resultSet.getString("direcao");
                String distancia = resultSet.getString("distancia");
                String obs = resultSet.getString("obs");

                Segmentos segmento = new Segmentos(id, pontoOrigem, pontoDestino, direcao, distancia, obs);
                segmentos.add(segmento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return segmentos;
    }

    public static boolean editarUsuario(int userId, String novoNome, String novoEmail, String novoTipo, String novaSenha) {
        String sql;
        if (novaSenha != null && !novaSenha.isEmpty()) {
            sql = "UPDATE usuarios SET nome = ?, email = ?, tipo = ?, senha = ? WHERE id = ?";
        } else {
            sql = "UPDATE usuarios SET nome = ?, email = ?, tipo = ? WHERE id = ?";
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, novoNome);
            preparedStatement.setString(2, novoEmail);
            preparedStatement.setString(3, novoTipo);

            if (novaSenha != null && !novaSenha.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
                preparedStatement.setString(4, hashedPassword);
                preparedStatement.setInt(5, userId);
            } else {
                preparedStatement.setInt(4, userId);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean excluirUsuario(int userId) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean excluirPonto(int pontoId) {
        String sql = "DELETE FROM pontos WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pontoId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean excluirSegmento(int segmentoId) {
        String sql = "DELETE FROM segmentos WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, segmentoId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User encontrarUsuarioPorEmailESenha(String email, String senha) {
        String sql = "SELECT id, nome, tipo, email, senha FROM usuarios WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPasswordBCrypt = resultSet.getString("senha");

                if (BCrypt.checkpw(senha, storedPasswordBCrypt)) {
                    int id = resultSet.getInt("id");
                    String nome = resultSet.getString("nome");
                    String tipo = resultSet.getString("tipo");
                    String userEmail = resultSet.getString("email");

                    User user = new User(id, nome, tipo, userEmail);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public User encontrarUsuarioPorId(String userId) {
        String sql = "SELECT id, nome, tipo, email, senha FROM usuarios WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String tipo = resultSet.getString("tipo");
                String userEmail = resultSet.getString("email");

                User user = new User(id, nome, tipo, userEmail);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Pontos encontrarPontoPorId(String pontoId) {
        String sql = "SELECT id, nome, obs FROM pontos WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pontoId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String obs = resultSet.getString("obs");

                return (Pontos) new Pontos(id, nome, obs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Segmentos encontrarSegmentoPorId(String segmentoId) {
        String sql = "SELECT id, ponto_origem, ponto_destino, direcao, distancia, obs FROM segmentos WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, segmentoId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                Pontos pontoOrigem = resultSet.getObject("ponto_origem", Pontos.class);
                Pontos pontoDestino = resultSet.getObject("ponto_destino", Pontos.class);
                String direcao = resultSet.getString("direcao");
                String distancia = resultSet.getString("distancia");
                String obs = resultSet.getString("obs");

                return (Segmentos) new Segmentos(id, pontoOrigem, pontoDestino, direcao, distancia, obs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
