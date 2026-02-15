package model;

public class ProgressiveFine implements FineStrategy {
    @Override
    public double calculateFine(int overstayHours) {
        if (overstayHours <= 0) return 0.0;
        return 20.0 * overstayHours * overstayHours; // example progressive fine
    }
}