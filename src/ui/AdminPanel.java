package ui;

import controller.PaymentAndFineController;
import model.ParkingLot;
import model.Floor;
import model.ParkingSpot;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AdminPanel extends JPanel {

    private final int TOTSPOTS = 144; //total all spots in system

    private JLabel occupancyLabel;
    private JLabel revenueLabel;
    private JTable finesTable;
    private PaymentAndFineController controller;
    private ParkingLot parkingLot;

    

    public AdminPanel(PaymentAndFineController controller) {
        this.controller = controller;
        this.parkingLot = ParkingLot.getInstance();
        
        setLayout(new BorderLayout(15, 15)); // like padding for the admin panel
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // kind of like settign the padding of the admin panel

        JLabel title = new JLabel("Admin Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        add(leftPanel(), BorderLayout.WEST);
        add(tabbedCenterPanel(), BorderLayout.CENTER);
    }

    // ---------------- LEFT PANEL ----------------
    private JPanel leftPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));  // Changed from 7 to 8 for refresh button
        panel.setBorder(new TitledBorder("System Summary"));

        // Real occupancy from ParkingLot
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
        
        // ADD REFRESH BUTTON
        JButton btnRefresh = new JButton("🔄 Refresh Occupancy");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> {
            updateOccupancyDisplay();
            revalidate();
            repaint();
            JOptionPane.showMessageDialog(this, 
                "Occupancy refreshed!\nCurrent: " + parkingLot.getOccupiedSpots() + " / " + parkingLot.getTotalSpots(), 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(btnRefresh);

        return panel;
    }

    // ADD THIS NEW METHOD
    /**
     * Updates the occupancy label with real data from ParkingLot
     */
    private void updateOccupancyDisplay() {
        int occupied = parkingLot.getOccupiedSpots();
        int total = parkingLot.getTotalSpots();
        double percentage = parkingLot.getOccupancyRate();
        
        String displayText = String.format("Occupancy Rate: %d / %d (%.1f%%)", 
            occupied, total, percentage);
        
        if (occupancyLabel == null) {
            occupancyLabel = new JLabel(displayText);
        } else {
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

    // ---------------- PARKED VEHICLES ----------------
    private JPanel parkedVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Vehicles Currently Parked"));

        JTable table = new JTable(
                new Object[][]{},
                new String[]{"Plate", "Vehicle Type", "Spot", "Entry Time", "Ticket"}
        );

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ---------------- UNPAID FINES ----------------
    private JPanel unpaidFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Outstanding Fines"));

        JTable table = new JTable(
                new Object[][]{},
                new String[]{"Plate", "Amount (RM)"}
        );

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
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