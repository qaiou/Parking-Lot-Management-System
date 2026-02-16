package ui;

import controller.PaymentAndFineController;
import model.*; // Imports Vehicle, Car, Motorcycle, Ticket, ParkingSpot, etc.

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class EntryExitPanel extends JPanel {

    private JPanel resultPanel;
    private JTextField entryPlate, exitPlate;
    private JTextArea billArea;
    private JComboBox<String> vehicleTypeBox;
    private JComboBox<String> preferredSpotBox;
    private JCheckBox vipCheckBox;
    
    private PaymentAndFineController controller;
    private ParkingLot parkingLot;

    // Temporary variables to store bill data between "Calculate" and "Pay"
    private double currentUsageFee = 0.0;
    private int currentDurationHours = 0;
    private String currentPlate = "";
    private String currentSpotId = "";

    public EntryExitPanel(PaymentAndFineController controller) {
        this.controller = controller;
        this.parkingLot = ParkingLot.getInstance();
        
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Entry / Exit Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicle Entry", entryPanel());
        tabs.addTab("Vehicle Exit", exitPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ---------------- ENTRY TAB ----------------
    private JPanel entryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Entry"));

        entryPlate = new JTextField();
        
        // Dropdown matching your specific Vehicle classes
        vehicleTypeBox = new JComboBox<>(new String[]{
                "Motorcycle", "Car", "SUV/Truck", "Handicapped Vehicle", "Electric Vehicle"
        });
        
        // Dropdown matching SpotType.java
        preferredSpotBox = new JComboBox<>(new String[]{
                "Compact", "Regular", "Handicapped", "Reserved", "Electric Charging"
        });
        
        vipCheckBox = new JCheckBox("VIP Customer");

        JButton btnSearch = new JButton("Show Available Spots");

        form.add(new JLabel("License Plate:"));
        form.add(entryPlate);
        form.add(new JLabel("Vehicle Type:"));
        form.add(vehicleTypeBox);
        form.add(new JLabel("Preferred Spot Type:"));
        form.add(preferredSpotBox);
        form.add(vipCheckBox);
        form.add(btnSearch);

        panel.add(form, BorderLayout.NORTH);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new TitledBorder("Available Spots"));

        panel.add(new JScrollPane(resultPanel), BorderLayout.CENTER);

        // --- SEARCH BUTTON LOGIC ---
        btnSearch.addActionListener(e -> {
            resultPanel.removeAll();

            String vehicleStr = (String) vehicleTypeBox.getSelectedItem();
            String preferredStr = (String) preferredSpotBox.getSelectedItem();
            boolean isVip = vipCheckBox.isSelected();

            // 1. Basic UI Validation
            if (!isValidSelection(vehicleStr, preferredStr, isVip)) {
                return; // Error message handled in helper
            }

            // 2. Find Spots
            SpotType spotType = SpotType.fromString(preferredStr);
            if (spotType == null) {
                JOptionPane.showMessageDialog(this, "Invalid Spot Type", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<ParkingSpot> availableSpots = parkingLot.getAvailableSpotsByType(spotType);
            
            if (availableSpots.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No available " + preferredStr + " spots found.",
                    "Parking Full",
                    JOptionPane.WARNING_MESSAGE
                );
                resultPanel.revalidate();
                resultPanel.repaint();
                return;
            }
            
            // 3. Display Spots grouped by Floor
            int currentFloor = -1;
            JPanel rowPanel = null;
            
            for (ParkingSpot spot : availableSpots) {
                // Parse floor from ID (e.g. "F1-R1-S1")
                int floorNum = 1; 
                try {
                    floorNum = Integer.parseInt(spot.getSpotId().substring(1, spot.getSpotId().indexOf('-')));
                } catch (Exception ex) { /* use default */ }

                if (floorNum != currentFloor) {
                    currentFloor = floorNum;
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    rowPanel.setBorder(new TitledBorder("Floor " + floorNum));
                    resultPanel.add(rowPanel);
                }
                
                // Add the button for this spot
                JButton spotBtn = spotBox(spot);
                if (rowPanel != null) {
                    rowPanel.add(spotBtn);
                }
            }

            resultPanel.revalidate();
            resultPanel.repaint();
        });

        return panel;
    }

    /**
     * Validates User Selection before searching.
     * Note: ParkingSpot.occupySpot() does the FINAL check, this is just for UI feedback.
     */
    private boolean isValidSelection(String vehicle, String spot, boolean isVip) {
        // VIP Check
        if (spot.equals("Reserved") && !isVip) {
            JOptionPane.showMessageDialog(this, "Only VIPs can use Reserved spots!", "Restricted", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Mismatch Checks
        if (vehicle.equals("Motorcycle") && !spot.equals("Compact")) {
            JOptionPane.showMessageDialog(this, "Motorcycles must use Compact spots.", "Invalid", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (vehicle.equals("SUV/Truck") && spot.equals("Compact")) {
            JOptionPane.showMessageDialog(this, "SUVs/Trucks are too big for Compact spots.", "Invalid", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (vehicle.equals("Electric Vehicle") && !spot.equals("Electric Charging")) {
             int confirm = JOptionPane.showConfirmDialog(this, 
                 "EV selected but not using a Charging spot. Continue?", "Confirm", JOptionPane.YES_NO_OPTION);
             if (confirm == JOptionPane.NO_OPTION) return false;
        }
        if (spot.equals("Electric Charging") && !vehicle.equals("Electric Vehicle")) {
            JOptionPane.showMessageDialog(this, "Only EVs can use Charging spots.", "Invalid", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }

    // ---------------- EXIT TAB ----------------
    private JPanel exitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Exit"));

        exitPlate = new JTextField();
        JComboBox<String> paymentMethodBox = new JComboBox<>(new String[]{"Cash", "Card"});

        JButton btnCalculate = new JButton("Calculate Bill");
        JButton btnPay = new JButton("Process Payment");

        form.add(new JLabel("License Plate:"));
        form.add(exitPlate);
        form.add(new JLabel("Payment Method:"));
        form.add(paymentMethodBox);
        form.add(btnCalculate);
        form.add(btnPay);

        panel.add(form, BorderLayout.NORTH);

        billArea = new JTextArea();
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billArea.setEditable(false);
        panel.add(new JScrollPane(billArea), BorderLayout.CENTER);

        // --- CALCULATE BILL LOGIC ---
        btnCalculate.addActionListener(e -> {
            String plate = exitPlate.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Plate Number");
                return;
            }

            // Find Vehicle
            ParkingSpot spot = parkingLot.findVehicleSpot(plate);
            if (spot == null) {
                billArea.setText("Vehicle not found in the parking lot.");
                return;
            }

            Vehicle v = spot.getCurrentVehicle();
            if (v == null || v.getTicket() == null) {
                billArea.setText("Error: Vehicle data corrupted.");
                return;
            }

            // Calculate Duration
            LocalDateTime entryTime = v.getEntryTime();
            LocalDateTime exitTime = LocalDateTime.now();
            Duration dur = Duration.between(entryTime, exitTime);
            long hours = (long) Math.ceil(dur.toMinutes() / 60.0);
            if (hours == 0) hours = 1; // Minimum 1 hour

            // Calculate Usage Fee
            double rate = spot.getHourlyRate();
            double fee = hours * rate;

            // Store for Payment step
            currentUsageFee = fee;
            currentDurationHours = (int) hours;
            currentPlate = plate;
            currentSpotId = spot.getSpotId();

            // Display Preview
            StringBuilder sb = new StringBuilder();
            sb.append("=== BILL PREVIEW ===\n");
            sb.append("Plate: ").append(plate).append("\n");
            sb.append("Spot:  ").append(spot.getSpotId()).append(" (").append(spot.getTypeName()).append(")\n");
            sb.append("Entry: ").append(entryTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
            sb.append("Exit:  ").append(exitTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
            sb.append("Duration: ").append(hours).append(" hours\n");
            sb.append("Rate: RM ").append(rate).append("/hr\n");
            sb.append("Parking Fee: RM ").append(String.format("%.2f", fee)).append("\n");
            sb.append("\n[NOTE] Overstay fines (if any) will be added\nautomatically upon payment.");
            
            billArea.setText(sb.toString());
        });

        // --- PROCESS PAYMENT LOGIC ---
        btnPay.addActionListener(e -> {
            if (currentPlate.isEmpty() || !currentPlate.equals(exitPlate.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Please click 'Calculate Bill' first.");
                return;
            }

            String method = (String) paymentMethodBox.getSelectedItem();

            // 1. Call Controller to apply Strategy Pattern for Fines
            // (Friend's code: processExit calculates fine, updates revenue, clears DB)
            PaymentAndFine receipt = controller.processExit(currentPlate, currentUsageFee, currentDurationHours, method);

            // 2. Remove Vehicle from ParkingLot
            parkingLot.removeVehicle(currentSpotId);

            // 3. Show Final Receipt
            billArea.setText(""); // Clear preview
            StringBuilder sb = new StringBuilder();
            sb.append("=== PAYMENT RECEIPT ===\n");
            sb.append("Plate:       ").append(receipt.getPlateNumber()).append("\n");
            sb.append("Parking Fee: RM ").append(String.format("%.2f", receipt.getUsageFee())).append("\n");
            sb.append("Fines:       RM ").append(String.format("%.2f", receipt.getFineAmount())).append("\n");
            sb.append("-----------------------\n");
            sb.append("TOTAL PAID:  RM ").append(String.format("%.2f", receipt.getTotalAmount())).append("\n");
            sb.append("Method:      ").append(receipt.getPaymentMethod()).append("\n");
            sb.append("Status:      PAID & CLEARED\n");
            
            billArea.setText(sb.toString());

            JOptionPane.showMessageDialog(this, "Payment Successful! Gate Opening...");
            
            // Reset fields
            exitPlate.setText("");
            currentPlate = "";
        });

        return panel;
    }

    // ---------------- SPOT BUTTON FACTORY ----------------
    /**
     * Creates a spot button that knows how to create the correct Vehicle object.
     */
    private JButton spotBox(ParkingSpot spot) {
        JButton spotBtn = new JButton();
        spotBtn.setLayout(new GridLayout(4, 1));
        spotBtn.setFocusPainted(false);
        spotBtn.setBackground(new Color(0, 150, 0)); // Green for available
        spotBtn.setForeground(Color.WHITE);
        spotBtn.setFont(new Font("Arial", Font.PLAIN, 10));

        spotBtn.add(new JLabel(spot.getSpotId(), JLabel.CENTER));
        spotBtn.add(new JLabel(spot.getTypeName(), JLabel.CENTER));
        spotBtn.add(new JLabel("Available", JLabel.CENTER));
        spotBtn.add(new JLabel("RM " + spot.getHourlyRate() + "/hr", JLabel.CENTER));

        spotBtn.addActionListener(e -> {
            String plate = entryPlate.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Plate Number first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Book spot " + spot.getSpotId() + " for " + plate + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // 1. Instantiate the correct Vehicle Object
                Vehicle vehicle = createVehicleObject(plate);
                
                // 2. Create and Assign Ticket
                Ticket ticket = new Ticket(plate, spot.getSpotId());
                vehicle.setTicket(ticket);
                
                // 3. Park it (ParkingSpot.occupySpot will validate again)
                boolean success = parkingLot.parkVehicle(spot.getSpotId(), vehicle);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Ticket Generated: " + ticket.getTicketId() + "\nParked Successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the grid
                    resultPanel.removeAll();
                    resultPanel.revalidate();
                    resultPanel.repaint();
                    entryPlate.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to park. Spot rules violation (e.g. SUV in Compact).", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return spotBtn;
    }

    /**
     * Factory method to create Vehicle subclasses based on dropdown
     */
    private Vehicle createVehicleObject(String plate) {
        String typeStr = (String) vehicleTypeBox.getSelectedItem();
        
        switch (typeStr) {
            case "Motorcycle": return new Motorcycle(plate);
            case "Car": return new Car(plate);
            case "SUV/Truck": return new SUV(plate);
            case "Handicapped Vehicle": return new HandicappedVehicle(plate);
            case "Electric Vehicle": return new ElectricVehicle(plate);
            default: return new Car(plate); // Fallback
        }
    }
}