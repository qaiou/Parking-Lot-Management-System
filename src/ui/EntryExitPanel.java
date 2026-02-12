package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EntryExitPanel extends JPanel {

    public EntryExitPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Entry / Exit Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // -------- Tabs --------
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicle Entry", createEntryPanel());
        tabs.addTab("Vehicle Exit", createExitPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Entry"));

        JTextField txtPlate = new JTextField();
        JComboBox<String> vehicleTypeBox = new JComboBox<>(new String[]{
                "Motorcycle", "Car", "SUV/Truck", "Handicapped Vehicle"
        });
        JComboBox<String> spotTypeBox = new JComboBox<>(new String[]{
                "Compact", "Regular", "Handicapped", "Reserved"
        });

        form.add(new JLabel("License Plate:"));
        form.add(txtPlate);
        form.add(new JLabel("Vehicle Type:"));
        form.add(vehicleTypeBox);
        form.add(new JLabel("Preferred Spot Type:"));
        form.add(spotTypeBox);

        JButton btnSearchSpots = new JButton("Show Available Spots");
        JButton btnPark = new JButton("Park Vehicle");

        form.add(btnSearchSpots);
        form.add(btnPark);

        panel.add(form, BorderLayout.NORTH);

        JTable spotTable = new JTable(
                new Object[][]{},
                new String[]{"Spot ID", "Floor", "Type", "Status"}
        );
        panel.add(new JScrollPane(spotTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Exit"));

        JTextField txtPlate = new JTextField();
        JComboBox<String> paymentMethodBox = new JComboBox<>(new String[]{
                "Cash", "Card"
        });

        form.add(new JLabel("License Plate:"));
        form.add(txtPlate);
        form.add(new JLabel("Payment Method:"));
        form.add(paymentMethodBox);

        JButton btnCalculate = new JButton("Calculate Bill");
        JButton btnPay = new JButton("Process Payment");

        form.add(btnCalculate);
        form.add(btnPay);

        panel.add(form, BorderLayout.NORTH);

        JTextArea billArea = new JTextArea();
        billArea.setEditable(false);
        panel.add(new JScrollPane(billArea), BorderLayout.CENTER);

        return panel;
    }
}
