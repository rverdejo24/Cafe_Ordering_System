package gui;

import order.Order;
import payment.Payment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class CafeOrderingSystemGUI {
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

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(mainPanel);
        frame.add(buttonPanel);

        // --- Step 1: Create the orders.txt file when program runs ---
        try {
            File file = new File("orders.txt");
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
                String item = itemField.getText();
                String quantity = quantityField.getText();
                // TODO: Write the item and quantity into orders.txt
                JOptionPane.showMessageDialog(frame, "Order added (but not yet saved).");
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                itemField.setText("");
                quantityField.setText("");
            }
        });

        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
