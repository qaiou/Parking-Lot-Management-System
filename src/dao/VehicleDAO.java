package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import model.*;

public class VehicleDAO {

    public VehicleDAO() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS parked_vehicles (" +
                     "plate_number TEXT PRIMARY KEY, " + 
                     "vehicle_type TEXT, " + 
                     "spot_id TEXT, " + 
                     "entry_time TEXT)";
        try (Connection conn = DBConnect.getConnect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveVehicle(Vehicle v, String spotId) {
        String sql = "INSERT OR REPLACE INTO parked_vehicles (plate_number, vehicle_type, spot_id, entry_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, v.getPlateNumber());
            pstmt.setString(2, v.getType());
            pstmt.setString(3, spotId);
            pstmt.setString(4, v.getEntryTime().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeVehicle(String spotId) {
        String sql = "DELETE FROM parked_vehicles WHERE spot_id = ?";
        try (Connection conn = DBConnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Vehicle> loadAllVehicles() {
        Map<String, Vehicle> parkedVehicles = new HashMap<>();
        String sql = "SELECT * FROM parked_vehicles";
        try (Connection conn = DBConnect.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String plate = rs.getString("plate_number");
                String type = rs.getString("vehicle_type");
                String spotId = rs.getString("spot_id");
                
                Vehicle v = createVehicle(plate, type);
                parkedVehicles.put(spotId, v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parkedVehicles;
    }

    private Vehicle createVehicle(String plate, String type) {
        switch (type) {
            case "Motorcycle": return new Motorcycle(plate);
            case "Car": return new Car(plate);
            case "SUV/Truck": return new SUV(plate);
            case "Handicapped Vehicle": return new HandicappedVehicle(plate);
            case "Electric Vehicle": return new ElectricVehicle(plate);
            default: return new Car(plate);
        }
    }
}