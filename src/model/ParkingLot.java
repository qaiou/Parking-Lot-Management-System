package model; // nura

import java.util.ArrayList;
import java.util.List;

/**
 * Main ParkingLot class that manages the entire parking lot system.
 * This implements Requirement #1: Parking Lot Structure
 * 
 * The parking lot has:
 * - Multiple floors (default: 4 floors)
 * - Each floor has multiple parking spots arranged in rows
 * - Total spots: 4 floors × 24 spots/floor = 96 spots
 */
public class ParkingLot {
    
    private static ParkingLot instance;  // Singleton pattern for single parking lot
    private List<Floor> floors;
    private int totalFloors;
    
    /**
     * Private constructor to prevent direct instantiation (Singleton pattern)
     * @param numberOfFloors Number of floors in the parking lot
     */
    private ParkingLot(int numberOfFloors) {
        this.totalFloors = numberOfFloors;
        this.floors = new ArrayList<>();
        initializeFloors();
    }
    
    /**
     * Gets the singleton instance of ParkingLot
     * @param numberOfFloors Number of floors (only used on first call)
     * @return The single ParkingLot instance
     */
    public static ParkingLot getInstance(int numberOfFloors) {
        if (instance == null) {
            instance = new ParkingLot(numberOfFloors);
        }
        return instance;
    }
    
    /**
     * Gets the singleton instance with default 4 floors
     * @return The single ParkingLot instance
     */
    public static ParkingLot getInstance() {
        return getInstance(4);  // Default: 4 floors
    }
    
    /**
     * Initializes all floors in the parking lot
     */
    private void initializeFloors() {
        for (int i = 1; i <= totalFloors; i++) {
            floors.add(new Floor(i));
        }
    }
    
    /**
     * Gets all floors in the parking lot
     * @return List of all floors
     */
    public List<Floor> getAllFloors() {
        return new ArrayList<>(floors);  // Return copy
    }
    
    /**
     * Gets a specific floor by number
     * @param floorNumber Floor number (1-based)
     * @return Floor object, or null if not found
     */
    public Floor getFloor(int floorNumber) {
        if (floorNumber < 1 || floorNumber > totalFloors) {
            return null;
        }
        return floors.get(floorNumber - 1);  // Convert to 0-based index
    }
    
    /**
     * Finds a parking spot anywhere in the parking lot by its ID
     * @param spotId Spot ID (e.g., "F1-R1-S1")
     * @return ParkingSpot object, or null if not found
     */
    public ParkingSpot findSpotById(String spotId) {
        // Extract floor number from spot ID (e.g., "F1-R1-S1" -> floor 1)
        try {
            int floorNum = Integer.parseInt(spotId.substring(1, spotId.indexOf('-')));
            Floor floor = getFloor(floorNum);
            if (floor != null) {
                return floor.getSpotById(spotId);
            }
        } catch (Exception e) {
            // Invalid spot ID format
            return null;
        }
        return null;
    }
    
    /**
     * Gets all available spots of a specific type across all floors
     * @param type Type of spot to search for
     * @return List of available spots
     */
    public List<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (Floor floor : floors) {
            availableSpots.addAll(floor.getAvailableSpotsByType(type));
        }
        return availableSpots;
    }
    
    /**
     * Parks a vehicle in a specific spot
     * @param spotId Spot ID where to park
     * @param vehiclePlate License plate of the vehicle
     * @return true if successfully parked, false otherwise
     */
    public boolean parkVehicle(String spotId, String vehiclePlate) {
        ParkingSpot spot = findSpotById(spotId);
        if (spot == null) {
            return false;  // Spot not found
        }
        return spot.occupySpot(vehiclePlate);
    }
    
    /**
     * Removes a vehicle from a spot (when vehicle exits)
     * @param spotId Spot ID to release
     * @return License plate of the vehicle that left, or null if spot was empty
     */
    public String removeVehicle(String spotId) {
        ParkingSpot spot = findSpotById(spotId);
        if (spot == null || spot.isAvailable()) {
            return null;
        }
        return spot.releaseSpot();
    }
    
    /**
     * Finds which spot a vehicle is parked in
     * @param vehiclePlate License plate to search for
     * @return ParkingSpot where vehicle is parked, or null if not found
     */
    public ParkingSpot findVehicleSpot(String vehiclePlate) {
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                if (spot.isOccupied() && 
                    spot.getCurrentVehiclePlate().equalsIgnoreCase(vehiclePlate)) {
                    return spot;
                }
            }
        }
        return null;
    }
    
    /**
     * Gets the total number of spots in the entire parking lot
     * @return Total spots across all floors
     */
    public int getTotalSpots() {
        int total = 0;
        for (Floor floor : floors) {
            total += floor.getTotalSpots();
        }
        return total;
    }
    
    /**
     * Gets the total number of occupied spots
     * @return Count of occupied spots
     */
    public int getOccupiedSpots() {
        int occupied = 0;
        for (Floor floor : floors) {
            occupied += floor.getOccupiedCount();
        }
        return occupied;
    }
    
    /**
     * Gets the total number of available spots
     * @return Count of available spots
     */
    public int getAvailableSpots() {
        return getTotalSpots() - getOccupiedSpots();
    }
    
    /**
     * Calculates the overall occupancy rate of the parking lot
     * @return Occupancy percentage (0.0 to 100.0)
     */
    public double getOccupancyRate() {
        if (getTotalSpots() == 0) {
            return 0.0;
        }
        return ((double) getOccupiedSpots() / getTotalSpots()) * 100.0;
    }
    
    /**
     * Gets a summary string of the parking lot status
     * @return Status summary
     */
    public String getStatusSummary() {
        return String.format("Parking Lot: %d/%d occupied (%.1f%% full)", 
            getOccupiedSpots(), getTotalSpots(), getOccupancyRate());
    }
    
    public int getTotalFloors() {
        return totalFloors;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PARKING LOT STATUS ===\n");
        sb.append(getStatusSummary()).append("\n\n");
        for (Floor floor : floors) {
            sb.append(floor.toString()).append("\n");
        }
        return sb.toString();
    }
}