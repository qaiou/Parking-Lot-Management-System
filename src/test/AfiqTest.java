package test;

import controller.PaymentAndFineController;
import javax.swing.*;
import model.ParkingLot;
import ui.AdminPanel;
import ui.EntryExitPanel;

public class AfiqTest {
    public static void main(String[] args) {
        // Run on the Swing Event Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("--- STARTING TEST MODE ---");

                // 1. Initialize the Backend (Memory)
                // We create a standard 4-floor parking lot
                ParkingLot.getInstance(4); 
                
                // We need the controller (even if we don't use the DB yet, the panel needs it)
                PaymentAndFineController controller = new PaymentAndFineController();

                // 2. Create a simple Window frame
                JFrame frame = new JFrame("TESTING: My Modules (Entry/Exit + Admin)");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 700);
                frame.setLocationRelativeTo(null); // Center on screen

                // 3. Create a Tabbed Pane to switch between your views
                JTabbedPane tabs = new JTabbedPane();
                
                // Add YOUR Entry Panel
                EntryExitPanel entryPanel = new EntryExitPanel(controller);
                tabs.addTab("Customer View (Entry/Exit)", entryPanel);
                
                // Add YOUR Admin Panel (to check if data updates)
                AdminPanel adminPanel = new AdminPanel(controller);
                tabs.addTab("Admin View (Check Data)", adminPanel);

                frame.add(tabs);
                
                // 4. Launch!
                frame.setVisible(true);
                System.out.println("--- TEST WINDOW OPEN ---");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Test crash: " + e.getMessage());
            }
        });
    }
}