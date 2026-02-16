package model;

import java.util.ArrayList;
import java.util.List;

//Implements Singleton pattern to ensure only one parking lot exists.
 
public class ParkingLot {
    
    private static ParkingLot instance; 
    private List<Floor> floors;
    private int totalFloors;
    
    //Private constructor for Singleton
    private ParkingLot(int numberOfFloors) {
        this.totalFloors = numberOfFloors;
        this.floors = new ArrayList<>();
        initializeFloors();
    }
    
    //Get instance with specific number of floors (used for initialization)
    public static ParkingLot getInstance(int numberOfFloors) {
        if (instance == null) {
            instance = new ParkingLot(numberOfFloors);
        }
        return instance;
    }
    
    //Get instance with default (4) floors
    public static ParkingLot getInstance() {
        return getInstance(4); 
    }
    
    private void initializeFloors() {
        for (int i = 1; i <= totalFloors; i++) {
            floors.add(new Floor(i));
        }
    }
    
    // --- Core Functionality ---

    /**
     * Park a vehicle in a specific spot.
     * Delegates validation to ParkingSpot.occupySpot().
     * spotId the ID of the spot (e.g., "F1-R1-S1")
     * vehicle ---> Vehicle object to park
     * return true if successful, false if spot occupied or type mismatch
     */
    public boolean parkVehicle(String spotId, Vehicle vehicle) {
        ParkingSpot spot = findSpotById(spotId);
        if (spot == null) {
            return false; 
        }
        return spot.occupySpot(vehicle);
    }
    
    /**
     * Remove a vehicle from a specific spot.
     * spotId the ID of the spot
     * return the Vehicle object that left (needed for billing)
     */
    public Vehicle removeVehicle(String spotId) {
        ParkingSpot spot = findSpotById(spotId);
        if (spot == null || spot.isAvailable()) {
            return null;
        }
        return spot.releaseSpot();
    }
    
    // --- Search & Retrieval Methods ---

    public ParkingSpot findSpotById(String spotId) {
        try {
            // Assumes ID format F1-R1-S1. Parses "1" from "F1"
            int floorNum = Integer.parseInt(spotId.substring(1, spotId.indexOf('-')));
            Floor floor = getFloor(floorNum);
            if (floor != null) {
                return floor.getSpotById(spotId);
            }
        } catch (Exception e) {
            // Handle invalid ID formats gracefully
            return null;
        }
        return null;
    }
    
    public List<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (Floor floor : floors) {
            availableSpots.addAll(floor.getAvailableSpotsByType(type));
        }
        return availableSpots;
    }
    
    //Finds the spot containing a specific vehicle plate.
     //for Exit Panel when user only enters plate number.
     
    public ParkingSpot findVehicleSpot(String vehiclePlate) {
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                if (spot.isOccupied() && spot.getCurrentVehicle() != null) {
                    if (spot.getCurrentVehicle().getPlateNumber().equalsIgnoreCase(vehiclePlate)) {
                        return spot;
                    }
                }
            }
        }
        return null;
    }

    //Used to populate the "Parked Vehicles" table.
     
    public List<Vehicle> getAllParkedVehicles() {
        List<Vehicle> allVehicles = new ArrayList<>();
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                if (spot.isOccupied() && spot.getCurrentVehicle() != null) {
                    allVehicles.add(spot.getCurrentVehicle());
                }
            }
        }
        return allVehicles;
    }
    
    // --- Getters & Status ---

    public List<Floor> getAllFloors() {
        return new ArrayList<>(floors); 
    }
    
    public Floor getFloor(int floorNumber) {
        if (floorNumber < 1 || floorNumber > totalFloors) {
            return null;
        }
        return floors.get(floorNumber - 1); 
    }

    public int getTotalSpots() {
        int total = 0;
        for (Floor floor : floors) {
            total += floor.getTotalSpots();
        }
        return total;
    }
    
    public int getOccupiedSpots() {
        int occupied = 0;
        for (Floor floor : floors) {
            occupied += floor.getOccupiedCount();
        }
        return occupied;
    }
    
    public int getAvailableSpots() {
        return getTotalSpots() - getOccupiedSpots();
    }
    
    public double getOccupancyRate() {
        if (getTotalSpots() == 0) {
            return 0.0;
        }
        return ((double) getOccupiedSpots() / getTotalSpots()) * 100.0;
    }
    
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