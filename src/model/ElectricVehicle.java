package model;

public class ElectricVehicle extends Vehicle {

    public ElectricVehicle(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public String getType() {
        return "Electric Vehicle";
    }
}