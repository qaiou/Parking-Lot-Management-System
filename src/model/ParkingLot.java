package model;

import dao.VehicleDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main ParkingLot class that manages the entire parking lot system.
 * Implements Singleton pattern to ensure only one parking lot exists.
 * Updated to include persistence for vehicle entry and exit.
 */
public class ParkingLot {
    
    private static ParkingLot instance; 
    private List<Floor> floors;
    private int totalFloors;
    private VehicleDAO vehicleDao;

    /**
     * Private constructor for Singleton
     */
    private ParkingLot(int numberOfFloors) {
        this.totalFloors = numberOfFloors;
        this.floors = new ArrayList<>();
        this.vehicleDao = new VehicleDAO();
        initializeFloors();
        loadPersistedVehicles(); 
    }
    
    /**
     * Get instance with specific number of floors
     */
    public static ParkingLot getInstance(int numberOfFloors) {
        if (instance == null) {
            instance = new ParkingLot(numberOfFloors);
        }
        return instance;
    }
    
    /**
     * Get instance with default (4) floors
     */
    public static ParkingLot getInstance() {
        return getInstance(4); 
    }
    
    private void initializeFloors() {
        for (int i = 1; i <= totalFloors; i++) {
            floors.add(new Floor(i));
        }
    }

    /**
     * Loads previously saved vehicles from the database.
     * Recreates Ticket objects to prevent "corrupted data" errors in the Exit Panel.
     */
    private void loadPersistedVehicles() {
        Map<String, Vehicle> savedVehicles = vehicleDao.loadAllVehicles();
        for (Map.Entry<String, Vehicle> entry : savedVehicles.entrySet()) {
            String spotId = entry.getKey();
            Vehicle vehicle = entry.getValue();
            
            // Re-assign a valid Ticket to the loaded vehicle
            Ticket ticket = new Ticket(vehicle.getPlateNumber(), spotId);
            vehicle.setTicket(ticket);
            
            ParkingSpot spot = findSpotById(spotId);
            if (spot != null) {
                // Directly occupy the spot without repeating the save logic
                spot.occupySpot(vehicle);
            }
        }
    }
    
    // --- Core Functionality ---

    /**
     * Park a vehicle in a specific spot and saves the record to the database.
     */
    public boolean parkVehicle(String spotId, Vehicle vehicle) {
        ParkingSpot spot = findSpotById(spotId);
        if (spot == null) {
            return false; 
        }
        
        boolean success = spot.occupySpot(vehicle);
        if (success) {
            vehicleDao.saveVehicle(vehicle, spotId);
        }
        return success;
    }
    
    /**
     * Remove a vehicle from a specific spot and removes the record from the database.
     */
    public Vehicle removeVehicle(String spotId) {
        ParkingSpot spot = findSpotById(spotId);
        if (spot == null || spot.isAvailable()) {
            return null;
        }
        
        Vehicle vehicle = spot.releaseSpot();
        if (vehicle != null) {
            vehicleDao.removeVehicle(spotId);
        }
        return vehicle;
    }
    
    // --- Search & Retrieval Methods ---

    public ParkingSpot findSpotById(String spotId) {
        try {
            int floorNum = Integer.parseInt(spotId.substring(1, spotId.indexOf('-')));
            Floor floor = getFloor(floorNum);
            if (floor != null) {
                return floor.getSpotById(spotId);
            }
        } catch (Exception e) {
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