package test; // nura

import model.ParkingLot;
import model.Floor;
import model.ParkingSpot;
import model.SpotType;
import java.util.List;

/**
 * Simple test class to demonstrate the parking lot functionality
 * This shows how your backend model works independently of the UI
 */
public class ParkingLotTest {
    
    public static void main(String[] args) {
        System.out.println("=== PARKING LOT SYSTEM TEST ===\n");
        
        // Get the parking lot instance (Singleton pattern)
        ParkingLot parkingLot = ParkingLot.getInstance(4);
        
        // Show initial status
        System.out.println("1. INITIAL STATUS:");
        System.out.println(parkingLot.getStatusSummary());
        System.out.println("Total Spots: " + parkingLot.getTotalSpots());
        System.out.println("Occupied: " + parkingLot.getOccupiedSpots());
        System.out.println("Available: " + parkingLot.getAvailableSpots());
        System.out.println();
        
        // Test 1: Park some vehicles
        System.out.println("2. PARKING VEHICLES:");
        parkingLot.parkVehicle("F1-R1-S1", "ABC1234");
        System.out.println("Parked ABC1234 in F1-R1-S1");
        
        parkingLot.parkVehicle("F1-R3-S2", "XYZ5678");
        System.out.println("Parked XYZ5678 in F1-R3-S2");
        
        parkingLot.parkVehicle("F2-R4-S5", "DEF9999");
        System.out.println("Parked DEF9999 in F2-R4-S5");
        System.out.println();
        
        // Show updated status
        System.out.println("3. UPDATED STATUS:");
        System.out.println(parkingLot.getStatusSummary());
        System.out.println();
        
        // Test 2: Find available spots by type
        System.out.println("4. AVAILABLE COMPACT SPOTS:");
        List<ParkingSpot> compactSpots = parkingLot.getAvailableSpotsByType(SpotType.COMPACT);
        System.out.println("Found " + compactSpots.size() + " available compact spots");
        if (compactSpots.size() > 0) {
            System.out.println("First 5 compact spots:");
            for (int i = 0; i < Math.min(5, compactSpots.size()); i++) {
                System.out.println("  - " + compactSpots.get(i).getSpotId());
            }
        }
        System.out.println();
        
        // Test 3: Find where a vehicle is parked
        System.out.println("5. FIND VEHICLE:");
        ParkingSpot spot = parkingLot.findVehicleSpot("ABC1234");
        if (spot != null) {
            System.out.println("Vehicle ABC1234 is parked in: " + spot.getSpotId());
            System.out.println("Spot type: " + spot.getTypeName());
            System.out.println("Hourly rate: RM " + spot.getHourlyRate());
        }
        System.out.println();
        
        // Test 4: Remove a vehicle
        System.out.println("6. VEHICLE EXIT:");
        String removedPlate = parkingLot.removeVehicle("F1-R1-S1");
        System.out.println("Removed vehicle: " + removedPlate);
        System.out.println(parkingLot.getStatusSummary());
        System.out.println();
        
        // Test 5: Show floor-by-floor status
        System.out.println("7. FLOOR-BY-FLOOR STATUS:");
        for (Floor floor : parkingLot.getAllFloors()) {
            System.out.println(floor.toString());
        }
        System.out.println();
        
        // Test 6: Try to park in occupied spot
        System.out.println("8. TEST OCCUPIED SPOT:");
        boolean result = parkingLot.parkVehicle("F1-R3-S2", "NEWCAR");
        if (result) {
            System.out.println("Successfully parked NEWCAR");
        } else {
            System.out.println("Failed to park NEWCAR (spot already occupied)");
        }
        System.out.println();
        
        System.out.println("=== TEST COMPLETE ===");
    }
}