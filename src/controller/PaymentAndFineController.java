package controller;

import dao.FineDAO;
import model.*;   // imports FineCalculator, FineStrategy, FixedFine, HourlyFine, ProgressiveFine, PaymentAndFine

public class PaymentAndFineController {
    private FineCalculator fineCalculator;
    private FineDAO fineDAO;
    private double totalRevenue;

    public PaymentAndFineController() {
        // Default fine strategy is FixedFine
        this.fineCalculator = new FineCalculator(new FixedFine());
        this.fineDAO = new FineDAO();
        this.totalRevenue = 0.0;
    }

    // Switch fine strategy dynamically
    public void setFineStrategy(FineStrategy strategy) {
        fineCalculator.setFineStrategy(strategy);
    }

    // Process exit: calculate fine, usage fee, and payment
    public PaymentAndFine processExit(String plate, double usageFee, int overstayHours, String paymentMethod) {
        double fine = fineCalculator.calculateFine(overstayHours);
        double total = usageFee + fine;

        // Clear fines from DB if paid
        if (fine > 0) {
            fineDAO.clearFine(plate);
        }

        // Update revenue
        totalRevenue += total;

        // Return a PaymentAndFine object for receipt generation
        return new PaymentAndFine(plate, usageFee, fine, total, paymentMethod);
    }

    // Add a fine (e.g., detected by system)
    public void addFine(String plate, double amount) {
        fineDAO.addFine(plate, amount);
    }

    // Get unpaid fine for a plate
    public double getUnpaidFine(String plate) {
        return fineDAO.getUnpaidFine(plate);
    }

    // Get all unpaid fines (for AdminPanel table)
    public java.util.Map<String, Double> getAllUnpaidFines() {
        return fineDAO.getAllUnpaidFines();
    }

    // Get total revenue
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public FineDAO getFineDAO() {
        return fineDAO;
    }
}