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

    public static void initializeDatabase() {
        try (Connection conn = getConnect();
            Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS parked_vehicles (
                    plate_number TEXT PRIMARY KEY,
                    vehicle_type TEXT NOT NULL,
                    spot_id TEXT NOT NULL,
                    entry_time TEXT NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plate TEXT NOT NULL,
                    amount REAL NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plate TEXT NOT NULL,
                    amount REAL NOT NULL,
                    payment_time TEXT NOT NULL
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
}
