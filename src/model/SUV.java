package model;

public class SUV extends Vehicle {

    public SUV(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public String getType() {
        return "SUV/Truck";
    }
}