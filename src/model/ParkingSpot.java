package model;

// Represents a single parking spot in the parking lot.
// Stores the specific Vehicle object when occupied and enforces parking rules.
public class ParkingSpot {
    
    private String spotId;          
    private SpotType type;          
    private boolean isOccupied;     
    private Vehicle currentVehicle;
    private double hourlyRate;      
    
    public ParkingSpot(String spotId, SpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.isOccupied = false;
        this.currentVehicle = null;
        this.hourlyRate = type.getRate(); 
    }
    
    // Tries to park a vehicle in this spot.
    // Enforces rules about which vehicles can park in which spot types.
    // Returns true if successful, false if rule violation or spot occupied.
    public boolean occupySpot(Vehicle vehicle) {
        if (isOccupied) {
            return false;
        }

        String vType = vehicle.getType();

        // Motorcycle can ONLY park in Compact spots
        if (vType.equals("Motorcycle") && this.type != SpotType.COMPACT) {
            return false;
        }

        // Car can only park in Compact or Regular spots
        if (vType.equals("Car") && this.type != SpotType.COMPACT && this.type != SpotType.REGULAR) {
            return false;
        }

        // SUV/Truck can ONLY park in Regular spots
        if (vType.equals("SUV/Truck") && this.type != SpotType.REGULAR) {
            return false;
        }

        // Only Handicapped Vehicles can use Handicapped spots
        // Normal vehicles cannot park in Handicapped spots
        if (this.type == SpotType.HANDICAPPED && !vType.equals("Handicapped Vehicle")) {
            return false;
        }

        // Only Electric Vehicles can use Electric spots
        if (this.type == SpotType.ELECTRIC && !vType.equals("Electric Vehicle")) {
            return false;
        }

        // Handicapped Vehicle parking in a Handicapped spot = FREE (RM 0/hr)
        // As per requirement: FREE only if handicapped card holder parks in handicapped spot
        if (vType.equals("Handicapped Vehicle") && this.type == SpotType.HANDICAPPED) {
            this.hourlyRate = 0.0;
        } else if (vType.equals("Handicapped Vehicle")) {
            // Handicapped vehicle parking elsewhere pays RM 2/hr (same as handicapped rate)
            this.hourlyRate = SpotType.HANDICAPPED.getRate();
        } else {
            // All other vehicles pay the spot's normal rate
            this.hourlyRate = this.type.getRate();
        }

        this.isOccupied = true;
        this.currentVehicle = vehicle;
        return true;
    }
    
    // Removes the vehicle from this spot.
    // Returns the Vehicle object that was removed, used for calculating fees.
    public Vehicle releaseSpot() {
        Vehicle leavingVehicle = this.currentVehicle;
        this.isOccupied = false;
        this.currentVehicle = null;
        this.hourlyRate = this.type.getRate(); // Reset rate back to default
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
    
    // Returns the plate number safely, null if spot is empty
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