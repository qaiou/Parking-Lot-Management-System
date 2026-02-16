package dao;

import java.sql.*;

public class PaymentDAO {

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

    public void processPayment(PaymentAndFine payment) {
        String sql = "INSERT INTO payments (plate, amount, payment_time) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getConnect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, payment.getPlate());
            ps.setDouble(2, payment.getTotalAmount());
            ps.setString(3, payment.getPaymentTime().toString()); // or formatted string
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalRevenue += payment.getTotalAmount();
    }

    
}
