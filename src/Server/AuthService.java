package Server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickAuth(String login, String pass) {
        String sql = String.format("SELECT nickname\n" +
                "  FROM users\n" +
                " WHERE login = '%s' AND \n" +
                "       password = '%s';", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String registerNewLogin(String login, String nick, String pass) {
        try {
            String sql = String.format("SELECT * FROM users\n" +
                    "WHERE nickname = '%s'", nick);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("nickname").equals(nick)) {
                    return "Nickname is busy";
                }
            }

            sql = String.format("SELECT * FROM users\n" +
                    "WHERE login = '%s'", login);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("login").equals(login)) {
                    return "Login is busy";
                }
            }

            sql = "SELECT Count(*) FROM users";
            rs = stmt.executeQuery(sql);
            int id_num = rs.getInt(1);

            sql = String.format("INSERT INTO users (id, login, nickname, password)\n"+
                    "VALUES (%s, '%s', '%s', '%s')", id_num+1, login, nick, pass);

            int fff = stmt.executeUpdate(sql);
            if (fff == 1){
                return "New user added";
            } else {
                return "Something went wrong";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Something went really wrong";
        }
//        return null;
    }

}
