package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

public class EntryExitPanel extends JPanel {

    private JPanel resultPanel;

    public EntryExitPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Entry / Exit Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicle Entry", createEntryPanel());
        tabs.addTab("Vehicle Exit", createExitPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ---------------- ENTRY ----------------
    private JPanel createEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Entry"));

        JTextField txtPlate = new JTextField();
        JComboBox<String> vehicleTypeBox = new JComboBox<>(new String[]{
                "Motorcycle", "Car", "SUV/Truck", "Handicapped Vehicle"
        });
        JComboBox<String> preferredSpotBox = new JComboBox<>(new String[]{
                "Compact", "Regular", "Handicapped", "Reserved"
        });

        JButton btnSearch = new JButton("Show Available Spots");

        form.add(new JLabel("License Plate:"));
        form.add(txtPlate);
        form.add(new JLabel("Vehicle Type:"));
        form.add(vehicleTypeBox);
        form.add(new JLabel("Preferred Spot Type:"));
        form.add(preferredSpotBox);
        form.add(new JLabel());
        form.add(btnSearch);

        panel.add(form, BorderLayout.NORTH);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new TitledBorder("Available Spots"));

        panel.add(new JScrollPane(resultPanel), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            resultPanel.removeAll();

            String vehicle = (String) vehicleTypeBox.getSelectedItem();
            String preferred = (String) preferredSpotBox.getSelectedItem();

            if (!isValid(vehicle, preferred)) {
                JOptionPane.showMessageDialog(
                        this,
                        vehicle + " cannot park in " + preferred + " spots.",
                        "Invalid Selection",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String[] rowTypes = {"Handicapped", "Reserved", "Compact", "Regular"};
            double[] rates = {2.0, 10.0, 2.0, 5.0};

            int rowIndex = switch (preferred) {
                case "Handicapped" -> 0;
                case "Reserved" -> 1;
                case "Compact" -> 2;
                default -> 3;
            };

            for (int floor = 1; floor <= 4; floor++) {

                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                rowPanel.setBorder(new TitledBorder("Floor " + floor + " - " + preferred + " Row"));

                for (int spot = 1; spot <= 6; spot++) {
                    String id = "F" + floor + "-R" + (rowIndex + 1) + "-S" + spot;
                    JButton b = createSpotBox(id, rowTypes[rowIndex], rates[rowIndex], true);
                    rowPanel.add(b);
                }

                resultPanel.add(rowPanel);
            }

            revalidate();
            repaint();
        });

        return panel;
    }

    private boolean isValid(String vehicle, String spot) {
        if (vehicle.equals("Motorcycle") && !spot.equals("Compact")) return false;
        if (vehicle.equals("SUV/Truck") && !spot.equals("Regular")) return false;
        return true;
    }

    // ---------------- EXIT ----------------
    private JPanel createExitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Vehicle Exit"));

        JTextField txtPlate = new JTextField();
        JComboBox<String> paymentMethodBox = new JComboBox<>(new String[]{
                "Cash", "Card"
        });
        JTextField cashField = new JTextField();
        cashField.setEnabled(false);

        JButton btnCalculate = new JButton("Calculate Bill");
        JButton btnPay = new JButton("Process Payment");

        form.add(new JLabel("License Plate:"));
        form.add(txtPlate);
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
