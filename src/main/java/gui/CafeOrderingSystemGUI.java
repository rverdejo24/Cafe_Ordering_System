package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CafeOrderingSystemGUI {
    public static final Path ORDER_FILE = Paths.get("orders.txt");
    public static void main(String[] args) {
        JFrame frame = new JFrame("Café Ordering System");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainPanelConstraints = new GridBagConstraints();
        mainPanelConstraints.insets = new Insets(5, 10, 5, 10);
        mainPanelConstraints.anchor = GridBagConstraints.WEST;

        JLabel itemLabel = new JLabel("Item Name:");
        JTextField itemField = new JTextField(10);

        // 1st Row: Item Name Label
        mainPanelConstraints.gridx = 0;
        mainPanelConstraints.gridy = 0;
        mainPanelConstraints.fill = GridBagConstraints.NONE;
        mainPanelConstraints.weightx = 0;

        mainPanel.add(itemLabel, mainPanelConstraints);

        // 1st Row: Item Name Field
        mainPanelConstraints.gridx = 1;
        mainPanelConstraints.gridy = 0;
        mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanelConstraints.weightx = 1;

        mainPanel.add(itemField, mainPanelConstraints);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(5);

        // 2nd Row: Quantity Label
        mainPanelConstraints.gridx = 0;
        mainPanelConstraints.gridy = 1;
        mainPanelConstraints.fill = GridBagConstraints.NONE;
        mainPanelConstraints.weightx = 0;

        mainPanel.add(quantityLabel, mainPanelConstraints);

        // 2nd Row: Quantity Field
        mainPanelConstraints.gridx = 1;
        mainPanelConstraints.gridy = 1;
        mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanelConstraints.weightx = 1;

        mainPanel.add(quantityField, mainPanelConstraints);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add to Order");
        JButton clearButton = new JButton("Clear");
        JButton viewOrderButton = new JButton("View Orders");

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(viewOrderButton);

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(mainPanel);
        frame.add(buttonPanel);

        // --- Step 1: Create the orders.txt file when program runs ---
        initOrderFile(frame);

        // Add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String item = itemField.getText().trim();
                String quantity = quantityField.getText().trim();

                String errorMsg = validate(item, quantity);
                if (!errorMsg.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (writeOrder(item, quantity)) {
                    JOptionPane.showMessageDialog(frame, "\"" + item + "\" added to order.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    itemField.setText("");
                    quantityField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to save order.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Clear button
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                itemField.setText("");
                quantityField.setText("");
            }
        });

        // View order button
        viewOrderButton.addActionListener(_ -> {
            String orders = readOrders();
            if (orders == null) {
                JOptionPane.showMessageDialog(frame, "Failed to read orders.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, orders, "Current Orders", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // File operations
    private static void initOrderFile(JFrame frame) {
        try {
            File file = new File(ORDER_FILE.toString());

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Could not initialize orders file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean writeOrder(String item, String quantity) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDER_FILE.toFile(), true))) {
            bw.write("============================\nItem: " + item + "\nQuantity: " + quantity + "\n" );
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    private static String readOrders() {
        StringBuilder orders = new StringBuilder();
        orders.append(String.format("%-20s %s%n", "Item", "Qty"));
        orders.append("-".repeat(30)).append("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_FILE.toFile()))) {
            String line;
            String item = null;
            int count = 0;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Item: ")) {
                    item = line.replace("Item: ", "").trim();
                } else if (line.startsWith("Quantity: ") && item != null) {
                    String quantity = line.replace("Quantity: ", "").trim();
                    orders.append(String.format("%-20s %s%n", item, quantity));
                    item = null;
                    count++;
                }
            }

            if (count == 0) return "No orders yet.";

            orders.append("-".repeat(30));
            orders.append(String.format("%n%-20s %d item(s)", "Total:", count));
            return orders.toString();

        } catch (IOException e) {
            return null; // caller handles the error dialog
        }
    }

    // Validation
    private static String validate(String itemField, String quantityField) {
        StringBuilder sb = new StringBuilder();

        if (itemField.isEmpty()) {
            sb.append("Please enter an item name.\n");
        }

        if (quantityField.isEmpty()) {
            sb.append("Please enter a quantity.");
        } else {
            try {
                int qty = Integer.parseInt(quantityField);
                if (qty <= 0) throw new NumberFormatException();

            } catch (NumberFormatException e) {
                sb.append("Quantity must be a positive whole number.");
            }
        }

        return sb.toString();
    }
}
