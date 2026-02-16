package app;

import controller.*;
import dao.DBConnect;

import java.awt.*;
import javax.swing.*;
import ui.AdminPanel;
import ui.EntryExitPanel;
import ui.ReportingPanel;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            DBConnect.initializeDatabase();

            PaymentAndFineController controller = new PaymentAndFineController();
            ReportController reportController = new ReportController();

            JFrame frame = new JFrame("Parking Lot Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Admin Panel", new AdminPanel(controller));
            tabs.addTab("Entry / Exit", new EntryExitPanel(controller));
            tabs.addTab("Reporting", new ReportingPanel(reportController));

            frame.add(tabs, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
