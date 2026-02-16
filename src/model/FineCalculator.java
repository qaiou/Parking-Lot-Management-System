 package model;
 
 public class FineCalculator {
    private FineStrategy fineStrategy;

    public FineCalculator(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    public double calculateFine(int overstayHours) {
        return fineStrategy.calculateFine(overstayHours);
    }
}