package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Database instance;
    private  final String URL = "jdbc:mysql://localhost:3306/3b9";
    private final String USER = "root";
    private final String PASSWORD = "";
    private Connection con;

    private Database() {
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    public Connection getConnection() {
        return con;
    }
}

