package model; //nura

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single floor in the parking lot.
 * Each floor contains multiple rows of parking spots.
 * 
 * As per Requirement #1:
 * - Each floor has multiple parking spots arranged in rows
 * - Different row types: Handicapped, Reserved, Compact, Regular
 */
public class Floor {
    
    private int floorNumber;
    private List<ParkingSpot> spots;  // All spots on this floor
    private static final int SPOTS_PER_ROW = 6;  // 6 spots per row
    private static final int ROWS_PER_FLOOR = 4;  // 4 rows per floor
    
    /**
     * Constructor creates a floor with all its parking spots
     * @param floorNumber Floor number (1, 2, 3, 4, ...)
     */
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        initializeSpots();
    }
    
    /**
     * Initializes all parking spots on this floor.
     * Row 1: Handicapped spots (6 spots)
     * Row 2: Reserved spots (6 spots)
     * Row 3: Compact spots (6 spots)
     * Row 4: Regular spots (6 spots)
     * Total: 24 spots per floor
     */
    private void initializeSpots() {
        SpotType[] rowTypes = {
            SpotType.HANDICAPPED,  // Row 1
            SpotType.RESERVED,     // Row 2
            SpotType.COMPACT,      // Row 3
            SpotType.REGULAR       // Row 4
        };
        
        // Create spots for each row
        for (int row = 1; row <= ROWS_PER_FLOOR; row++) {
            for (int spot = 1; spot <= SPOTS_PER_ROW; spot++) {
                // Format: F1-R1-S1 (Floor 1, Row 1, Spot 1)
                String spotId = String.format("F%d-R%d-S%d", floorNumber, row, spot);
                SpotType type = rowTypes[row - 1];  // Array is 0-indexed
                
                ParkingSpot parkingSpot = new ParkingSpot(spotId, type);
                spots.add(parkingSpot);
            }
        }
    }
    
    /**
     * Gets all parking spots on this floor
     * @return List of all spots
     */
    public List<ParkingSpot> getAllSpots() {
        return new ArrayList<>(spots);  // Return copy to prevent external modification
    }
    
    /**
     * Gets available spots of a specific type
     * @param type Type of spot to search for
     * @return List of available spots of the given type
     */
    public List<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.getType() == type && spot.isAvailable()) {
                availableSpots.add(spot);
            }
        }
        return availableSpots;
    }
    
    /**
     * Finds a parking spot by its ID
     * @param spotId Spot ID (e.g., "F1-R1-S1")
     * @return ParkingSpot object, or null if not found
     */
    public ParkingSpot getSpotById(String spotId) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }
    
    /**
     * Gets the total number of spots on this floor
     * @return Total spots (always 24 per floor)
     */
    public int getTotalSpots() {
        return spots.size();
    }
    
    /**
     * Gets the number of occupied spots on this floor
     * @return Count of occupied spots
     */
    public int getOccupiedCount() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets the number of available spots on this floor
     * @return Count of available spots
     */
    public int getAvailableCount() {
        return getTotalSpots() - getOccupiedCount();
    }
    
    /**
     * Gets the occupancy rate for this floor as a percentage
     * @return Occupancy percentage (0.0 to 100.0)
     */
    public double getOccupancyRate() {
        return ((double) getOccupiedCount() / getTotalSpots()) * 100.0;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    @Override
    public String toString() {
        return String.format("Floor %d: %d/%d occupied (%.1f%%)", 
            floorNumber, getOccupiedCount(), getTotalSpots(), getOccupancyRate());
    }
}