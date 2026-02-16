package ui;

import controller.PaymentAndFineController;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.*; // Imported new Vehicle class
import javax.swing.border.TitledBorder;  // Imported new Ticket class
import javax.swing.table.DefaultTableModel;
import model.Floor;
import model.ParkingLot;
import model.ParkingSpot;
import model.Ticket;
import model.Vehicle; // Needed for dynamic tables

public class AdminPanel extends JPanel {

    private final int TOTSPOTS = 144; 

    private JLabel occupancyLabel;
    private JLabel revenueLabel;
    
    // CHANGED: Promoted models to class fields so we can refresh them
    private DefaultTableModel parkedVehiclesModel;
    private DefaultTableModel unpaidFinesModel;
    
    private PaymentAndFineController controller;
    private ParkingLot parkingLot;

    public AdminPanel(PaymentAndFineController controller) {
        this.controller = controller;
        this.parkingLot = ParkingLot.getInstance();
        
        setLayout(new BorderLayout(15, 15)); 
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 

        JLabel title = new JLabel("Admin Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        add(leftPanel(), BorderLayout.WEST);
        add(tabbedCenterPanel(), BorderLayout.CENTER);
    }

    // ---------------- LEFT PANEL ----------------
    private JPanel leftPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10)); 
        panel.setBorder(new TitledBorder("System Summary"));

        // Initialize label
        occupancyLabel = new JLabel("Loading...");
        updateOccupancyDisplay();

        revenueLabel = new JLabel("Total Revenue: RM 0.00");

        panel.add(occupancyLabel);
        panel.add(revenueLabel);

        panel.add(new JLabel("Fine Scheme:"));

        JComboBox<String> fineSchemeBox = new JComboBox<>(new String[]{
                "Fixed Fine Scheme",
                "Progressive Fine Scheme",
                "Hourly Fine Scheme"
        });

        JButton btnApply = new JButton("Apply Scheme");
        btnApply.addActionListener(e -> {
            String scheme = (String) fineSchemeBox.getSelectedItem();
            // TODO: Connect this to controller.setFineStrategy() if needed
            JOptionPane.showMessageDialog(this,
                    "Fine scheme applied: " + scheme,
                    "Scheme Updated", JOptionPane.INFORMATION_MESSAGE);
            });

        panel.add(fineSchemeBox);
        panel.add(btnApply);
        
        // UPDATED REFRESH BUTTON
        JButton btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> {
            refreshAllData(); // Now refreshes tables too
            JOptionPane.showMessageDialog(this, 
                "System data refreshed successfully!", 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(btnRefresh);

        return panel;
    }

    /**
     * Helper to refresh all admin data at once
     */
    private void refreshAllData() {
        // 1. Refresh Occupancy Label
        updateOccupancyDisplay();
        
        // 2. Refresh Revenue Label
        revenueLabel.setText(String.format("Total Revenue: RM %.2f", controller.getTotalRevenue()));
        
        // 3. Refresh Parked Vehicles Table
        loadParkedVehiclesData();
        
        // 4. Refresh Unpaid Fines Table
        loadUnpaidFinesData();
        
        revalidate();
        repaint();
    }

    private void updateOccupancyDisplay() {
        int occupied = parkingLot.getOccupiedSpots();
        int total = parkingLot.getTotalSpots();
        double percentage = parkingLot.getOccupancyRate();
        
        String displayText = String.format("Occupancy Rate: %d / %d (%.1f%%)", 
            occupied, total, percentage);
        
        if (occupancyLabel != null) {
            occupancyLabel.setText(displayText);
        }
    }

    // ---------------- CENTER TABS ----------------
    private JTabbedPane tabbedCenterPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parking Lot Status", parkingLotView());
        tabs.addTab("Parked Vehicles", parkedVehiclesPanel());
        tabs.addTab("Unpaid Fines", unpaidFinesPanel());
        return tabs;
    }

    // ---------------- PARKING LOT GRID ----------------
    private JPanel parkingLotView() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new TitledBorder("Parking Lot Status"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Get all floors from the parking lot
        for (Floor floor : parkingLot.getAllFloors()) {
            JPanel floorPanel = new JPanel(new GridLayout(4, 6, 8, 8));
            floorPanel.setBorder(new TitledBorder("Floor " + floor.getFloorNumber()));

            // Get all spots from this floor and display them
            java.util.List<ParkingSpot> spots = floor.getAllSpots();
            for (ParkingSpot spot : spots) {
                JButton spotBtn = spotBox(spot, false);
                floorPanel.add(spotBtn);
            }

            mainPanel.add(floorPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        container.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        return container;
    }

    // ---------------- PARKED VEHICLES (IMPLEMENTED) ----------------
    private JPanel parkedVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Vehicles Currently Parked"));

        // Column Headers
        String[] columns = {"Plate Number", "Vehicle Type", "Spot ID", "Entry Time", "Ticket ID"};
        
        // Initialize Model
        parkedVehiclesModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(parkedVehiclesModel);

        // Load initial data
        loadParkedVehiclesData();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Fetches real data from ParkingLot and populates the table
     */
    private void loadParkedVehiclesData() {
        if (parkedVehiclesModel == null) return;
        
        // Clear existing rows
        parkedVehiclesModel.setRowCount(0);
        
        // Formatter for displaying time nicely
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Get list of ALL vehicles from the model
        java.util.List<Vehicle> vehicles = parkingLot.getAllParkedVehicles();
        
        for (Vehicle v : vehicles) {
            String plate = v.getPlateNumber();
            String type = v.getType();
            String time = v.getEntryTime().format(formatter);
            
            // Handle ticket and spot safely (in case of manual entry errors)
            Ticket t = v.getTicket();
            String ticketId = (t != null) ? t.getTicketId() : "N/A";
            String spotId = (t != null) ? t.getSpotId() : "Unknown";
            
            // Add row to table
            parkedVehiclesModel.addRow(new Object[]{plate, type, spotId, time, ticketId});
        }
    }

    // ---------------- UNPAID FINES (CONNECTED) ----------------
    private JPanel unpaidFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Outstanding Fines"));

        String[] columns = {"Plate Number", "Total Unpaid Amount (RM)"};
        unpaidFinesModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(unpaidFinesModel);
        
        // Load initial data
        loadUnpaidFinesData();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadUnpaidFinesData() {
        if (unpaidFinesModel == null) return;
        
        unpaidFinesModel.setRowCount(0);
        
        // Get data from Controller -> DAO
        Map<String, Double> fines = controller.getAllUnpaidFines();
        
        for (Map.Entry<String, Double> entry : fines.entrySet()) {
            unpaidFinesModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    // ---------------- SPOT BUTTON ----------------
    private JButton spotBox(ParkingSpot spot, boolean disableIfOccupied) {

        JButton spotBtn = new JButton();
        spotBtn.setLayout(new GridLayout(4, 1));
        spotBtn.setFocusPainted(false);
        spotBtn.setForeground(Color.WHITE);
        spotBtn.setFont(new Font("Arial", Font.PLAIN, 10));

        // Get REAL data from ParkingSpot object
        String spotId = spot.getSpotId();
        String type = spot.getTypeName();
        boolean occupied = spot.isOccupied();
        String statusText = spot.getStatusDisplay();
        double rate = spot.getHourlyRate();

        JLabel l1 = new JLabel(spotId, JLabel.CENTER);
        JLabel l2 = new JLabel(type, JLabel.CENTER);
        JLabel l3 = new JLabel(statusText, JLabel.CENTER);
        JLabel l4 = new JLabel("RM " + rate + "/hr", JLabel.CENTER);

        // Set color based on real occupancy status
        if (occupied) {
            spotBtn.setBackground(Color.RED);
        } else {
            spotBtn.setBackground(new Color(0, 150, 0));
        }

        if (disableIfOccupied && occupied) {
            spotBtn.setEnabled(false);
        }

        //add labels to button
        spotBtn.add(l1);
        spotBtn.add(l2);
        spotBtn.add(l3);
        spotBtn.add(l4);

        return spotBtn;
    }
}