package ui;

import controller.ReportController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportingPanel extends JPanel {

    private ReportController controller;

    public ReportingPanel(ReportController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Reporting Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Currently Parked Vehicles", createParkedVehiclesTab());
        tabs.addTab("Revenue Report", createRevenueTab());
        tabs.addTab("Occupancy Report", createOccupancyTab());
        tabs.addTab("Fine Report", createFineTab());

        add(tabs, BorderLayout.CENTER);
    }

    // ===============================
    // CURRENTLY PARKED VEHICLES
    // ===============================
    private JPanel createParkedVehiclesTab() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Plate", "Vehicle Type", "Spot ID", "Entry Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<String[]> data = controller.getParkedVehicles();

        for (String[] row : data) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    // ===============================
    //  REVENUE REPORT
    // ===============================
    private JPanel createRevenueTab() {

        JPanel panel = new JPanel(new BorderLayout());

        double totalRevenue = controller.getRevenue();

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setText("=== TOTAL REVENUE ===\n\nRM " + totalRevenue);

        panel.add(area, BorderLayout.CENTER);

        return panel;
    }

    // ===============================
    //  OCCUPANCY REPORT
    // ===============================
    private JPanel createOccupancyTab() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Floor", "Total Spots", "Occupied Spots", "Occupancy Rate"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<String[]> data = controller.getOccupancy();

        for (String[] row : data) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    // ===============================
    // FINE REPORT
    // ===============================
    private JPanel createFineTab() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Plate Number", "Total Unpaid Fine (RM)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<String[]> data = controller.getFineReport();

        for (String[] row : data) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }
}
