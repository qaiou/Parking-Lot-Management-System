package model;

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