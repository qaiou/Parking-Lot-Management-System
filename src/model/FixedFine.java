package model;

public class FixedFine implements FineStrategy {
    @Override
    public double calculateFine(int overstayHours) {
        return overstayHours > 0 ? 50.0 : 0.0; // example fixed fine
    }
}