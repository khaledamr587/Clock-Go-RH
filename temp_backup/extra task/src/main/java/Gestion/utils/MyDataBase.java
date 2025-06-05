package Gestion.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL = "jdbc:mysql://localhost:3306/3b9";
    private final String USER = "root";
    private final String PASSWORD = "";
    private Connection connection;
    private static MyDataBase instance;


    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed() || !connection.isValid(5)) {
                // Reconnect
                System.out.println("Reconnecting to database...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Reconnection successful!");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/reconnecting to database: " + e.getMessage());
            try {
                // Attempt to create a new connection regardless
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException ex) {
                System.err.println("Failed to reconnect: " + ex.getMessage());
            }
        }
        return connection;
    }
    
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed!");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
