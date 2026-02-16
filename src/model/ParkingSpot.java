package model; // nura

/**
 * Represents a single parking spot in the parking lot.
 * Each spot has a unique ID, type, status, hourly rate, and may contain a vehicle.
 * 
 * This class implements the core data structure for Requirement #1: Parking Lot Structure
 * - Spot ID (e.g., "F1-R1-S1")
 * - Type (compact, regular, handicapped, reserved)
 * - Status (available or occupied)
 * - Current vehicle (if occupied)
 * - Hourly rate (varies by type)
 */
public class ParkingSpot {
    
    // Spot properties as per requirement
    private String spotId;          // Format: "F1-R1-S1" (Floor-Row-Spot)
    private SpotType type;          // Type of parking spot
    private boolean isOccupied;     // Status: true = occupied, false = available
    private String currentVehiclePlate;  // License plate of parked vehicle (null if empty)
    private double hourlyRate;      // Rate varies by spot type
    
    /**
     * Constructor to initialize a parking spot
     * @param spotId Unique identifier in format "F1-R1-S1"
     * @param type Type of parking spot (COMPACT, REGULAR, HANDICAPPED, RESERVED)
     */
    public ParkingSpot(String spotId, SpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.isOccupied = false;  // Initially all spots are available
        this.currentVehiclePlate = null;  // No vehicle initially
        this.hourlyRate = type.getRate();  // Set rate based on type
    }
    
    /**
     * Marks this spot as occupied by a vehicle
     * @param vehiclePlate License plate of the vehicle parking here
     * @return true if successfully occupied, false if already occupied
     */
    public boolean occupySpot(String vehiclePlate) {
        if (isOccupied) {
            return false;  // Cannot occupy an already occupied spot
        }
        this.isOccupied = true;
        this.currentVehiclePlate = vehiclePlate;
        return true;
    }
    
    /**
     * Marks this spot as available (vehicle has left)
     * @return The plate number of the vehicle that left
     */
    public String releaseSpot() {
        String leavingVehicle = this.currentVehiclePlate;
        this.isOccupied = false;
        this.currentVehiclePlate = null;
        return leavingVehicle;
    }
    
    /**
     * Checks if this spot is available for parking
     * @return true if available, false if occupied
     */
    public boolean isAvailable() {
        return !isOccupied;
    }
    
    /**
     * Gets the status as a string for display
     * @return "Available" or "Occupied by [plate]"
     */
    public String getStatusDisplay() {
        if (isOccupied) {
            return "Occupied by " + currentVehiclePlate;
        }
        return "Available";
    }
    
    // Getters for all properties
    public String getSpotId() {
        return spotId;
    }
    
    public SpotType getType() {
        return type;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public String getCurrentVehiclePlate() {
        return currentVehiclePlate;
    }
    
    public double getHourlyRate() {
        return hourlyRate;
    }
    
    /**
     * Gets the type name as string for display
     * @return Type name (e.g., "Compact", "Regular")
     */
    public String getTypeName() {
        return type.getDisplayName();
    }
    
    @Override
    public String toString() {
        return String.format("Spot[%s, %s, %s, RM%.2f/hr]", 
            spotId, type.getDisplayName(), getStatusDisplay(), hourlyRate);
    }
}