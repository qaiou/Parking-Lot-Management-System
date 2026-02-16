package ui;

import controller.PaymentAndFineController;
// ⭐ ADD THESE 4 IMPORTS
import model.ParkingLot;
import model.ParkingSpot;
import model.SpotType;
import java.util.List;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class EntryExitPanel extends JPanel {

    private JPanel resultPanel;
    private JTextField entryPlate, exitPlate;
    
    private PaymentAndFineController controller;
    // ⭐ ADD THIS FIELD
    private ParkingLot parkingLot;

    public EntryExitPanel(PaymentAndFineController controller) {
        this.controller = controller;
        // ⭐ ADD THIS LINE
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

    // ---------------- ENTRY ----------------
    private JPanel entryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Entry"));

        entryPlate = new JTextField();
        JComboBox<String> vehicleTypeBox = new JComboBox<>(new String[]{
                "Motorcycle", "Car", "SUV/Truck", "Handicapped Vehicle"
        });
        JComboBox<String> preferredSpotBox = new JComboBox<>(new String[]{
                "Compact", "Regular", "Handicapped", "Reserved"
        });
        JCheckBox vipCheckBox = new JCheckBox("VIP Customer");


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

        // ⭐ REPLACE btnSearch.addActionListener WITH THIS VERSION
        btnSearch.addActionListener(e -> {
            resultPanel.removeAll();

            String vehicle = (String) vehicleTypeBox.getSelectedItem();
            String preferred = (String) preferredSpotBox.getSelectedItem();
            boolean isVip = vipCheckBox.isSelected();

            if (!isValid(vehicle, preferred, isVip)) {
                if (preferred.equals("Reserved") && !isVip) {
                    JOptionPane.showMessageDialog(
                        this,
                        "User must be registered as a VIP customer to book a reserved spot",
                        "Invalid Selection",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                JOptionPane.showMessageDialog(
                        this,
                        vehicle + " cannot park in " + preferred + " spots.",
                        "Invalid Selection",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Convert string to SpotType enum
            SpotType spotType = SpotType.fromString(preferred);
            
            // Get REAL available spots from ParkingLot
            List<ParkingSpot> availableSpots = parkingLot.getAvailableSpotsByType(spotType);
            
            if (availableSpots.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "No available " + preferred + " spots at the moment.",
                    "No Spots Available",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Group spots by floor for better display
            int currentFloor = 0;
            JPanel rowPanel = null;
            
            for (ParkingSpot spot : availableSpots) {
                // Extract floor number from spot ID (e.g., "F1-R1-S1" -> 1)
                int floorNum = Integer.parseInt(spot.getSpotId().substring(1, 2));
                
                if (floorNum != currentFloor) {
                    // New floor, create new panel
                    currentFloor = floorNum;
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    rowPanel.setBorder(new TitledBorder("Floor " + floorNum + " - " + preferred + " Spots"));
                    resultPanel.add(rowPanel);
                }
                
                JButton spotBtn = spotBox(spot, true);
                rowPanel.add(spotBtn);
            }

            revalidate();
            repaint();
        });

        return panel;
    }

    private boolean isValid(String vehicle, String spot, boolean isVip) {
        if (vehicle.equals("Motorcycle") && !spot.equals("Compact"))
            return false;
        if (vehicle.equals("Car") && !(spot.equals("Compact") || spot.equals("Regular") || spot.equals("Reserved")))
            return false;
        if (vehicle.equals("SUV/Truck") && !(spot.equals("Regular") || spot.equals("Reserved")))
            return false;
        if (spot.equals("Reserved") && !isVip)
            return false;
        return true;
    }

    // ---------------- EXIT ----------------
    private JPanel exitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Exit"));

        exitPlate = new JTextField();
        JComboBox<String> paymentMethodBox = new JComboBox<>(new String[]{
                "Cash", "Card"
        });
        JTextField cashField = new JTextField();
        cashField.setEnabled(false);

        JButton btnCalculate = new JButton("Calculate Bill");
        JButton btnPay = new JButton("Process Payment");

        form.add(new JLabel("License Plate:"));
        form.add(exitPlate);
        form.add(new JLabel("Payment Method:"));
        form.add(paymentMethodBox);
        form.add(new JLabel("Cash Amount (RM):"));
        form.add(cashField);
        form.add(btnCalculate);
        form.add(btnPay);

        panel.add(form, BorderLayout.NORTH);

        JTextArea billArea = new JTextArea();
        billArea.setEditable(false);
        panel.add(new JScrollPane(billArea), BorderLayout.CENTER);

        paymentMethodBox.addActionListener(e -> {
            String method = (String) paymentMethodBox.getSelectedItem();
            cashField.setEnabled(method.equals("Cash"));
        });

        return panel;
    }

    // ⭐ REPLACE THE ENTIRE spotBox() METHOD WITH THIS
    // ---------------- SPOT BUTTON (USES REAL PARKINGSPOT) ----------------
    /**
     * Creates a button for a parking spot with real data and booking functionality
     * @param spot Real ParkingSpot object
     * @param disableIfOccupied Whether to disable if occupied
     * @return JButton for the spot
     */
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
        double rate = spot.getHourlyRate();

        JLabel l1 = new JLabel(spotId, JLabel.CENTER);
        JLabel l2 = new JLabel(type, JLabel.CENTER);
        JLabel l3 = new JLabel("", JLabel.CENTER);
        JLabel l4 = new JLabel("RM " + rate + "/hr", JLabel.CENTER);

        if (occupied) {
            spotBtn.setBackground(Color.RED);
            l3.setText("Occupied by " + spot.getCurrentVehiclePlate());
        } else {
            spotBtn.setBackground(new Color(0, 150, 0));
            l3.setText("Available");
        }

        if (disableIfOccupied && occupied) {
            spotBtn.setEnabled(false);
        }

        // Add click listener for booking
        spotBtn.addActionListener(e -> {
            // Validate plate number entered
            if (entryPlate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter your vehicle plate number before selecting a spot",
                        "No Vehicle Plate Number",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Confirm selection
            int selectedOpt = JOptionPane.showConfirmDialog(
                this, 
                "Book spot " + spotId + " for vehicle " + entryPlate.getText().trim() + "?", 
                "Confirm Spot Selection",
                JOptionPane.YES_NO_OPTION);

            if (selectedOpt == JOptionPane.YES_OPTION) {
                String enteredPlate = entryPlate.getText().trim();
                
                // ACTUALLY park the vehicle in the ParkingLot system
                boolean success = parkingLot.parkVehicle(spotId, enteredPlate);
                
                if (success) {
                    // Update UI to show occupied
                    spotBtn.setBackground(Color.RED);
                    l3.setText("Occupied by " + enteredPlate);
                    spotBtn.setEnabled(false);
                    
                    spotBtn.revalidate();
                    spotBtn.repaint();
                    
                    JOptionPane.showMessageDialog(
                        this,
                        "Vehicle " + enteredPlate + " successfully parked in spot " + spotId,
                        "Parking Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // Clear the plate field for next vehicle
                    entryPlate.setText("");
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to park vehicle. Spot may already be occupied.",
                        "Parking Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Add labels to button
        spotBtn.add(l1);
        spotBtn.add(l2);
        spotBtn.add(l3);
        spotBtn.add(l4);

        return spotBtn;
    }
}
