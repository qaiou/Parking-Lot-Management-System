package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

public class AdminPanel extends JPanel {

    public AdminPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Parking Overview", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        add(createControlPanel(), BorderLayout.WEST);
        add(createParkingLotView(), BorderLayout.CENTER); // now returns JPanel
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBorder(new TitledBorder("Actions"));

        panel.add(new JButton("View Occupancy Rate"));
        panel.add(new JButton("View Revenue"));
        panel.add(new JButton("View Parked Vehicles"));
        panel.add(new JButton("View Unpaid Fines"));

        JComboBox<String> fineSchemeBox = new JComboBox<>(new String[]{
                "Fixed Fine Scheme",
                "Progressive Fine Scheme",
                "Hourly Fine Scheme"
        });

        JButton btnApply = new JButton("Apply Fine Scheme");

        panel.add(fineSchemeBox);
        panel.add(btnApply);

        return panel;
    }

    private JPanel createParkingLotView() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new TitledBorder("Parking Lot Status"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Example: 2 floors, each 2 rows, 6 spots per row
        for (int floor = 1; floor <= 2; floor++) {

            JPanel floorPanel = new JPanel(new GridLayout(2, 6, 8, 8));
            floorPanel.setBorder(new TitledBorder("Floor " + floor));

            for (int i = 1; i <= 12; i++) {
                JButton spot = createSpotBox("F" + floor + "-S" + i);
                floorPanel.add(spot);
            }

            mainPanel.add(floorPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        container.add(scrollPane, BorderLayout.CENTER);

        return container; // ✅ now a JPanel
    }

    private JButton createSpotBox(String spotId) {
        JButton spot = new JButton(spotId);
        spot.setForeground(Color.WHITE);
        spot.setFocusPainted(false);

        // Random status for UI demo
        boolean occupied = new Random().nextBoolean();

        if (occupied) {
            spot.setBackground(Color.RED);
            spot.setToolTipText("Occupied");
        } else {
            spot.setBackground(new Color(0, 150, 0));
            spot.setToolTipText("Available");
        }

        return spot;
    }
}
