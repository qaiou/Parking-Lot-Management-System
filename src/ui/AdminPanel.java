package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

public class AdminPanel extends JPanel {

    private JLabel occupancyLabel;
    private JLabel revenueLabel;

    public AdminPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Admin Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        add(createLeftPanel(), BorderLayout.WEST);
        add(createTabbedCenterPanel(), BorderLayout.CENTER);
    }

    // ---------------- LEFT PANEL ----------------
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBorder(new TitledBorder("System Summary"));

        occupancyLabel = new JLabel("Occupancy Rate: 72 / 144 (50%)");
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

        panel.add(fineSchemeBox);
        panel.add(btnApply);

        return panel;
    }

    // ---------------- CENTER TABS ----------------
    private JTabbedPane createTabbedCenterPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parking Lot Status", createParkingLotView());
        tabs.addTab("Parked Vehicles", createParkedVehiclesPanel());
        tabs.addTab("Unpaid Fines", createUnpaidFinesPanel());
        return tabs;
    }

    // ---------------- PARKING LOT GRID ----------------
    private JPanel createParkingLotView() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new TitledBorder("Parking Lot Status"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        String[] rowTypes = {"Handicapped", "Reserved", "Compact", "Regular"};
        double[] rates = {2.0, 10.0, 2.0, 5.0};

        for (int floor = 1; floor <= 4; floor++) {

            JPanel floorPanel = new JPanel(new GridLayout(4, 6, 8, 8));
            floorPanel.setBorder(new TitledBorder("Floor " + floor));

            for (int row = 0; row < 4; row++) {
                for (int spot = 1; spot <= 6; spot++) {

                    String spotId = "F" + floor + "-R" + (row + 1) + "-S" + spot;
                    JButton spotBtn = createSpotBox(spotId, rowTypes[row], rates[row], false);
                    floorPanel.add(spotBtn);
                }
            }

            mainPanel.add(floorPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        container.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        return container;
    }

    // ---------------- PARKED VEHICLES ----------------
    private JPanel createParkedVehiclesPanel() {
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
    private JPanel createUnpaidFinesPanel() {
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
    private JButton createSpotBox(String spotId, String type, double rate, boolean disableIfOccupied) {

        JButton spot = new JButton();
        spot.setLayout(new GridLayout(4, 1));
        spot.setFocusPainted(false);
        spot.setForeground(Color.WHITE);
        spot.setFont(new Font("Arial", Font.PLAIN, 10));

        boolean occupied = new Random().nextBoolean();
        String plate = occupied ? "ABC1234" : null;

        JLabel l1 = new JLabel(spotId, JLabel.CENTER);
        JLabel l2 = new JLabel(type, JLabel.CENTER);
        JLabel l3 = new JLabel(
                occupied ? "Occupied by " + plate : "Available",
                JLabel.CENTER
        );
        JLabel l4 = new JLabel("RM " + rate + "/hr", JLabel.CENTER);

        if (occupied) {
            spot.setBackground(Color.RED);
        } else {
            spot.setBackground(new Color(0, 150, 0));
        }

        if (disableIfOccupied && occupied) {
            spot.setEnabled(false);
        }

        spot.add(l1);
        spot.add(l2);
        spot.add(l3);
        spot.add(l4);

        return spot;
    }
}
