package server;

import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

@Component
public class DbController implements Closeable {
    private Connection connection;
    private Statement statement;
    private static final String USERS = "CREATE TABLE IF NOT EXISTS USERS\n" +
            "(\n" +
            "    ID         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    LOGIN       TEXT NOT NULL,\n" +
            "    PASSWORD    TEXT NOT NULL,\n" +
            "    NICKNAME    TEXT NOT NULL\n" +
            ");";

    public DbController() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this. connection = DriverManager.getConnection("jdbc:sqlite:clientsDB");
        this.statement = connection.createStatement();
        statement.execute(USERS);
    }

    public boolean isAuthSuccess(String login, String password) throws SQLException {
        String sql = String.format("SELECT * FROM USERS WHERE LOGIN = '%s' AND PASSWORD = '%s';", login, password);
        return statement.executeQuery(sql).next();
    }

    public String getNickNameFromDb(String login) throws SQLException {
        String sql = String.format("SELECT NICKNAME FROM USERS WHERE LOGIN = '%s';", login);
        return statement.executeQuery(sql).getString("NICKNAME");
    }


    public void updateUser(String login, String password, String nickName) throws SQLException {
        String sql = String.format("INSERT INTO USERS (LOGIN, PASSWORD, NICKNAME) VALUES ('%s', '%s', '%s')", login, password, nickName);
        statement.execute(sql);
    }

    @Override
    public void close() throws IOException {
        try {
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
