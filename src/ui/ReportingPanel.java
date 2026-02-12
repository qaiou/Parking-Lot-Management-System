package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ReportingPanel extends JPanel {

    public ReportingPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Reporting Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // -------- Tabs --------
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Current Vehicles", createVehicleReport());
        tabs.addTab("Revenue Report", createRevenueReport());
        tabs.addTab("Occupancy Report", createOccupancyReport());
        tabs.addTab("Fine Report", createFineReport());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createVehicleReport() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Vehicles Currently Parked"));

        JTable table = new JTable(
                new Object[][]{},
                new String[]{"License Plate", "Vehicle Type", "Spot ID", "Entry Time"}
        );
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh");
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRevenueReport() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Revenue Report"));

        JTextArea revenueArea = new JTextArea();
        revenueArea.setEditable(false);

        JButton btnGenerate = new JButton("Generate Revenue Report");

        panel.add(new JScrollPane(revenueArea), BorderLayout.CENTER);
        panel.add(btnGenerate, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOccupancyReport() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Occupancy Report"));

        JTable table = new JTable(
                new Object[][]{},
                new String[]{"Floor", "Total Spots", "Occupied Spots", "Occupancy Rate"}
        );

        JButton btnRefresh = new JButton("Refresh");

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFineReport() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Outstanding Fine Report"));

        JTable table = new JTable(
                new Object[][]{},
                new String[]{"License Plate", "Fine Amount", "Reason", "Status"}
        );

        JButton btnRefresh = new JButton("Refresh");

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }
}
