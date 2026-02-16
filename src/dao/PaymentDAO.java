package dao;

import java.sql.*;
import java.time.LocalDateTime;

public class PaymentDAO {

    public void insertPayment(String plate, double amount) {

        String sql = "INSERT INTO payments (plate, amount, payment_time) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plate);
            ps.setDouble(2, amount);
            ps.setString(3, LocalDateTime.now().toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public double getTotalRevenue() {
        String sql = "SELECT SUM(amount) FROM payments";

        try (Connection conn = DBConnect.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
