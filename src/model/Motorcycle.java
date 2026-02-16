package model;

public class Motorcycle extends Vehicle {

    public Motorcycle(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public String getType() {
        return "Motorcycle";
    }
}