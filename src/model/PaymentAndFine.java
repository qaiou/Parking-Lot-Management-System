package model;

interface FineStrategy {
    double calculateFine(int overstayHours);
}

class FixedFine implements FineStrategy {
    @Override
    public double calculateFine(int overstayHours) {
        return 50.00; // Flat RM 50 fine
    }
}

class HourlyFine implements FineStrategy {
    @Override
    public double calculateFine(int overstayHours) {
        return overstayHours * 20.00; // RM 20 per hour
    }
}

class ProgressiveFine implements FineStrategy {
    @Override
    public double calculateFine(int overstayHours) {
        if (overstayHours <= 24) return 50.00;
        else if (overstayHours <= 48) return 150.00;
        else if (overstayHours <= 72) return 300.00;
        else return 500.00; // capped
    }
}

class FineCalculator {
    private FineStrategy fineStrategy;

    FineCalculator(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    double calculateFine(int overstayHours) {
        return fineStrategy.calculateFine(overstayHours);
    }
}

public class PaymentAndFine {
    private String plate;
    private double usageFee;
    private double fines;
    private double unpaidFines;
    private String paymentMethod;

    public PaymentAndFine(String plate, double usageFee, double fines, double unpaidFines, String paymentMethod) {
        this.plate = plate;
        this.usageFee = usageFee;
        this.fines = fines;
        this.unpaidFines = unpaidFines;
        this.paymentMethod = paymentMethod;
    }

    public double getTotal() {
        return usageFee + fines + unpaidFines;
    }

    public String generateReceipt() {
        return "Receipt for Vehicle: " + plate + "\n"
             + "Usage Fee: RM " + usageFee + "\n"
             + "Fines: RM " + fines + "\n"
             + "Unpaid Fines: RM " + unpaidFines + "\n"
             + "Total Paid: RM " + getTotal() + "\n"
             + "Payment Method: " + paymentMethod + "\n"
             + "Thank you!";
    }
}