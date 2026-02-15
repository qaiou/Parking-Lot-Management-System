package controller;

import dao.FineDAO;
import model.*;

public class PaymentAndFineController {
    private FineCalculator fineCalculator;
    private FineDAO fineDAO;

    public PaymentAndFineController(FineCalculator calculator, FineDAO dao) {
        this.fineCalculator = calculator;
        this.fineDAO = dao;
    }

    public PaymentAndFine processExit(String plate, double usageFee, int overstayHours, String paymentMethod) {
        double fines = fineCalculator.calculateFine(overstayHours);
        double unpaidFines = fineDAO.getUnpaidFine(plate);

        PaymentAndFine paymentAndFine = new PaymentAndFine(plate, usageFee, fines, unpaidFines, paymentMethod);

        fineDAO.clearFine(plate); // clear after payment
        return paymentAndFine;
    }

    public FineDAO getFineDAO() {
        return fineDAO;
    }

    public void setFineStrategy(FineStrategy strategy) {
        fineCalculator.setFineStrategy(strategy);
    }
}
