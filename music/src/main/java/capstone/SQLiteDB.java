package capstone;
import java.sql.*;

public class SQLiteDB {
    String connectionUrl;
    Connection connection;
    public SQLiteDB() {
        connectionUrl = "jdbc:sqlite:music//data//songs.db";
        try {
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println("database connected! ^_^");
        } catch(SQLException e) {
            System.out.println(e);
        }
    }
}
