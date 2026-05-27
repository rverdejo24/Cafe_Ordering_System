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
        JButton viewOrderButton = new JButton("View Order");

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(viewOrderButton);

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(mainPanel);
        frame.add(buttonPanel);

        // --- Step 1: Create the orders.txt file when program runs ---
        try {
            File file = new File(ORDER_FILE.toString());
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
        }

        // --- Step 2: Add button action (incomplete) ---
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String item = itemField.getText().trim();
                String quantity = quantityField.getText().trim();

                String errorMsg = errors(item, quantity);

                if (!errorMsg.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate quantity
                try {
                    int qty = Integer.parseInt(quantity);

                    if (qty <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Quantity must be a positive number.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Write items to text file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDER_FILE.toFile(), true))) {
                    bw.write("============================\n");
                    bw.write("Item Name: " + item + "\nQuantity: " + quantity + "\n");
                    JOptionPane.showMessageDialog(frame, item + " added to orders.txt", "Success",  JOptionPane.INFORMATION_MESSAGE);
                    itemField.setText("");
                    quantityField.setText("");
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(frame, "An error occurred while writing the file.");
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                itemField.setText("");
                quantityField.setText("");
            }
        });

        viewOrderButton.addActionListener(_ -> {
            StringBuilder orders = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new FileReader(ORDER_FILE.toFile()))) {
                String line;
                String item = null;
                String quantity;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("Item Name: ")) {
                        item = line.replace("Item Name: ", "").trim();
                    }

                    if (line.startsWith("Quantity: ")) {
                        quantity = line.replace("Quantity: ", "").trim();

                        orders.append("============================\n").append("Item Name: ").append(item).append("\n").append("Quantity: ").append(quantity).append("\n");
                    }
                }

                if (orders.isEmpty()) {
                    orders.append("No items found");
                }

                JOptionPane.showMessageDialog(frame, orders.toString(), "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(frame, "An error occurred while reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static String errors(String itemField, String quantityField) {
        StringBuilder sb = new StringBuilder();

        if (itemField.isEmpty()) {
            sb.append("Please enter an item name.\n");
        }

        if (quantityField.isEmpty()) {
            sb.append("Please enter a quantity.");
        }
        return sb.toString();
    }
}
