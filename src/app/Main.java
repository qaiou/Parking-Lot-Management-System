package app;

import controller.PaymentAndFineController;
import java.awt.*;
import javax.swing.*;
import ui.AdminPanel;
import ui.EntryExitPanel;
import ui.ReportingPanel;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            PaymentAndFineController controller = new PaymentAndFineController();

            JFrame frame = new JFrame("Parking Lot Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Admin Panel", new AdminPanel(controller));
            tabs.addTab("Entry / Exit", new EntryExitPanel(controller));
            tabs.addTab("Reporting", new ReportingPanel());

            frame.add(tabs, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
