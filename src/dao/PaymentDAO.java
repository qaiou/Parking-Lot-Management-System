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
}
