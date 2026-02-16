package model;

public class Car extends Vehicle {

    public Car(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public String getType() {
        return "Car";
    }
}