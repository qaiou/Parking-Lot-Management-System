package ui;

import controller.PaymentAndFineController;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.Floor;
import model.ParkingLot;
import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;

public class AdminPanel extends JPanel {

    private final int TOTSPOTS = 144; 

    private JLabel occupancyLabel;
    private JLabel revenueLabel;
    
    // FIX: Class-level declaration so all methods can access the grid container
    private JPanel parkingGridContainer; 
    
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
            JOptionPane.showMessageDialog(this,
                    "Fine scheme applied: " + scheme,
                    "Scheme Updated", JOptionPane.INFORMATION_MESSAGE);
            });

        panel.add(fineSchemeBox);
        panel.add(btnApply);
        
        // UPDATED REFRESH BUTTON: Synchronizes all UI components
        JButton btnRefresh = new JButton("🔄 Refresh All Data");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> {
            updateOccupancyDisplay();
            refreshParkingGrid(); 
            loadParkedVehiclesData();
            loadUnpaidFinesData();
            
            // Update revenue from controller
            revenueLabel.setText(String.format("Total Revenue: RM %.2f", controller.getTotalRevenue()));
            
            revalidate();
            repaint();
            JOptionPane.showMessageDialog(this, 
                "Admin View successfully synchronized with system data.", 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(btnRefresh);

        return panel;
    }

    /**
     * Updates the occupancy label with real data from ParkingLot
     */
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
    
    /**
     * Rebuilds the visual parking grid to show updated spot colors (Red/Green)
     */
    private void refreshParkingGrid() {
        if (parkingGridContainer != null) {
            parkingGridContainer.removeAll();
            
            for (Floor floor : parkingLot.getAllFloors()) {
                JPanel floorPanel = new JPanel(new GridLayout(4, 6, 8, 8));
                floorPanel.setBorder(new TitledBorder("Floor " + floor.getFloorNumber()));

                java.util.List<ParkingSpot> spots = floor.getAllSpots();
                for (ParkingSpot spot : spots) {
                    JButton spotBtn = spotBox(spot, false);
                    floorPanel.add(spotBtn);
                }

                parkingGridContainer.add(floorPanel);
                parkingGridContainer.add(Box.createVerticalStrut(15));
            }
            
            parkingGridContainer.revalidate();
            parkingGridContainer.repaint();
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

        parkingGridContainer = new JPanel(); 
        parkingGridContainer.setLayout(new BoxLayout(parkingGridContainer, BoxLayout.Y_AXIS));

        // Build initial visual grid
        refreshParkingGrid();

        container.add(new JScrollPane(parkingGridContainer), BorderLayout.CENTER);
        return container;
    }

    // ---------------- PARKED VEHICLES ----------------
    private JPanel parkedVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Vehicles Currently Parked"));

        String[] columns = {"Plate Number", "Vehicle Type", "Spot ID", "Entry Time", "Ticket ID"};
        
        parkedVehiclesModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(parkedVehiclesModel);

        loadParkedVehiclesData();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Fetches real data from ParkingLot and populates the table
     */
    private void loadParkedVehiclesData() {
        if (parkedVehiclesModel == null) return;
        
        parkedVehiclesModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        java.util.List<Vehicle> vehicles = parkingLot.getAllParkedVehicles();
        
        for (Vehicle v : vehicles) {
            String plate = v.getPlateNumber();
            String type = v.getType();
            String time = v.getEntryTime().format(formatter);
            
            Ticket t = v.getTicket();
            String ticketId = (t != null) ? t.getTicketId() : "N/A";
            String spotId = (t != null) ? t.getSpotId() : "Unknown";
            
            parkedVehiclesModel.addRow(new Object[]{plate, type, spotId, time, ticketId});
        }
    }

    // ---------------- UNPAID FINES ----------------
    private JPanel unpaidFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Outstanding Fines"));

        String[] columns = {"Plate Number", "Total Unpaid Amount (RM)"};
        unpaidFinesModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(unpaidFinesModel);
        
        loadUnpaidFinesData();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadUnpaidFinesData() {
        if (unpaidFinesModel == null) return;
        
        unpaidFinesModel.setRowCount(0);
        
        try {
            Map<String, Double> fines = controller.getAllUnpaidFines();
            if (fines != null) {
                for (Map.Entry<String, Double> entry : fines.entrySet()) {
                    unpaidFinesModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load fine data: " + e.getMessage());
        }
    }

    // ---------------- SPOT BUTTON ----------------
    private JButton spotBox(ParkingSpot spot, boolean disableIfOccupied) {

        JButton spotBtn = new JButton();
        spotBtn.setLayout(new GridLayout(4, 1));
        spotBtn.setFocusPainted(false);
        spotBtn.setForeground(Color.WHITE);
        spotBtn.setFont(new Font("Arial", Font.PLAIN, 10));

        String spotId = spot.getSpotId();
        String type = spot.getTypeName();
        boolean occupied = spot.isOccupied();
        String statusText = spot.getStatusDisplay();
        double rate = spot.getHourlyRate();

        JLabel l1 = new JLabel(spotId, JLabel.CENTER);
        JLabel l2 = new JLabel(type, JLabel.CENTER);
        JLabel l3 = new JLabel(statusText, JLabel.CENTER);
        JLabel l4 = new JLabel("RM " + rate + "/hr", JLabel.CENTER);

        if (occupied) {
            spotBtn.setBackground(Color.RED);
        } else {
            spotBtn.setBackground(new Color(0, 150, 0));
        }

        if (disableIfOccupied && occupied) {
            spotBtn.setEnabled(false);
        }

        spotBtn.add(l1);
        spotBtn.add(l2);
        spotBtn.add(l3);
        spotBtn.add(l4);

        return spotBtn;
    }
}