package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class FineDAO {

    // Get unpaid fine for a specific license plate
    public double getUnpaidFine(String plate) {
        double fine = 0.0;
        try (Connection conn = DBConnect.getConnect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT amount FROM fines WHERE plate = ?")) {
            ps.setString(1, plate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            fine += rs.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fine;
    }

    // Clear fines after payment
    public void clearFine(String plate) {
        try (Connection conn = DBConnect.getConnect();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM fines WHERE plate = ?")) {
            ps.setString(1, plate);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all unpaid fines (for Admin Panel table)
    public Map<String, Double> getAllUnpaidFines() {
        Map<String, Double> fines = new HashMap<>();
        try (Connection conn = DBConnect.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT plate, SUM(amount) as total FROM fines GROUP BY plate")) {
            while (rs.next()) {
                fines.put(rs.getString("plate"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    // Add a new fine (when detected)
    public void addFine(String plate, double amount) {
        try (Connection conn = DBConnect.getConnect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO fines (plate, amount) VALUES (?, ?)")) {
            ps.setString(1, plate);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
