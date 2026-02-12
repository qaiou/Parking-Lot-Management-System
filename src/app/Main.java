package app;

import ui.AdminPanel;
import ui.EntryExitPanel;
import ui.ReportingPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Parking Lot Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Admin Panel", new AdminPanel());
            tabs.addTab("Entry / Exit", new EntryExitPanel());
            tabs.addTab("Reporting", new ReportingPanel());

            frame.add(tabs, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
