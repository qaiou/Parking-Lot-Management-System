package dao; //pun takde error

import java.sql.*;

public class DBConnect {
    static String url = "jdbc:sqlite:parking.db";

    public static Connection getConnect() {
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(url);
            System.out.println("DB connected");
            return c;
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found:");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("DB connection failed:");
            e.printStackTrace();
            return null;
        }
    }

    
}
