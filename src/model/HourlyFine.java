package model;

public class HourlyFine implements FineStrategy {
    @Override
    public double calculateFine(int overstayHours) {
        return overstayHours * 10.0; // example hourly fine
    }
}