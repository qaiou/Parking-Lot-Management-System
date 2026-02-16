package controller;

import model.*;
import dao.*;
import java.util.*;

public class ReportController {

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private FineDAO fineDAO = new FineDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private ParkingLot parkingLot = ParkingLot.getInstance();

    public List<String[]> getParkedVehicles() {
        return vehicleDAO.getCurrentlyParkedVehicles();
    }

    public double getRevenue() {
        return paymentDAO.getTotalRevenue();
    }

    public List<String[]> getOccupancy() {
        return parkingLot.getOccupancyReport();
    }

    public List<String[]> getFineReport() {
        return fineDAO.getFineReport();
    }
}
