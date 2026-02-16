package model;

public class HandicappedVehicle extends Vehicle {

    public HandicappedVehicle(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public String getType() {
        return "Handicapped Vehicle";
    }
}