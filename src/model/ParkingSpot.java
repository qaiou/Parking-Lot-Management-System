package model;

/**
 * Represents a single parking spot in the parking lot.
 * Stores the specific Vehicle object when occupied and enforces parking rules.
 */
public class ParkingSpot {
    
    private String spotId;          
    private SpotType type;          
    private boolean isOccupied;     
    private Vehicle currentVehicle; // Stores the actual Vehicle object
    private double hourlyRate;      
    
    public ParkingSpot(String spotId, SpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.isOccupied = false;
        this.currentVehicle = null;
        this.hourlyRate = type.getRate(); 
    }
    
    /**
     * Tries to park a vehicle in this spot.
     * Enforces strict rules about which vehicles can park in which spot types.
     * * @param vehicle The Vehicle object to park.
     * @return true if successful, false if rule violation or spot occupied.
     */
    public boolean occupySpot(Vehicle vehicle) {
        if (isOccupied) {
            return false;
        }

        // --- DEFENSIVE CHECKS (Validation) ---
        
        // Rule 1: Only Handicapped Vehicles can use Handicapped Spots
        // (Note: Handicapped vehicles can park elsewhere, but normal cars can't park here)
        if (this.type == SpotType.HANDICAPPED && !vehicle.getType().equals("Handicapped Vehicle")) {
            return false;
        }

        // Rule 2: Only Electric Vehicles can use Electric Spots
        if (this.type == SpotType.ELECTRIC && !vehicle.getType().equals("Electric Vehicle")) {
            return false;
        }

        // Rule 3: Motorcycles can ONLY use Compact spots
        if (vehicle.getType().equals("Motorcycle") && this.type != SpotType.COMPACT) {
             return false;
        }

        // Rule 4: SUVs/Trucks CANNOT use Compact spots (Too big)
        if (vehicle.getType().equals("SUV/Truck") && this.type == SpotType.COMPACT) {
            return false;
        }

        // --- END CHECKS ---

        this.isOccupied = true;
        this.currentVehicle = vehicle;
        return true;
    }
    
    /**
     * Removes the vehicle from this spot.
     * @return The Vehicle object that was removed (useful for calculating fees).
     */
    public Vehicle releaseSpot() {
        Vehicle leavingVehicle = this.currentVehicle;
        this.isOccupied = false;
        this.currentVehicle = null;
        return leavingVehicle;
    }
    
    public boolean isAvailable() {
        return !isOccupied;
    }
    
    public String getStatusDisplay() {
        if (isOccupied && currentVehicle != null) {
            return "Occupied by " + currentVehicle.getPlateNumber();
        }
        return "Available";
    }
    
    // --- Getters ---

    public String getSpotId() {
        return spotId;
    }
    
    public SpotType getType() {
        return type;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }
    
    /**
     * Helper to get the plate number safely (returns null if empty).
     */
    public String getCurrentVehiclePlate() {
        if (currentVehicle != null) {
            return currentVehicle.getPlateNumber();
        }
        return null;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
    
    public String getTypeName() {
        return type.getDisplayName();
    }
    
    @Override
    public String toString() {
        return String.format("Spot[%s, %s, %s, RM%.2f/hr]", 
            spotId, type.getDisplayName(), getStatusDisplay(), hourlyRate);
    }
}