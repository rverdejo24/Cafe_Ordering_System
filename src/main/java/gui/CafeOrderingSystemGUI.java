package gui;

import javax.swing.*;
import java.awt.*;

public class CafeOrderingSystemGUI {
    static void main(String[] args) {
        JFrame frame = new JFrame("Cafe Ordering System");
        JLabel itemLabel = new JLabel("Item Name:");
        JTextField itemField = new JTextField(10);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(5);

        JButton addButton = new JButton("Add to Order");
        JButton clearButton = new JButton("Clear");

        frame.setLayout(new FlowLayout());
        frame.add(itemLabel);
        frame.add(itemField);
        frame.add(quantityLabel);
        frame.add(quantityField);
        frame.add(addButton);
        frame.add(clearButton);

        frame.setSize(350,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        addButton.addActionListener(_ -> {
           try {
               String item = itemField.getText().trim();
               if (item.isEmpty()) {
                   throw new IllegalArgumentException("Please enter an item name.");
               }

               int quantity = Integer.parseInt(quantityField.getText().trim());
               if (quantity < 1) {
                   throw new IllegalArgumentException("Quantity must be greater than 0.");
               }

               double price = 150.00;
               double total = price * quantity;

               JOptionPane.showMessageDialog(frame, "Item: " + item + "\nQuantity: " + quantity + "\nTotal: ₱" + total, "Order Summary", JOptionPane.INFORMATION_MESSAGE);
           } catch (NumberFormatException ex) {
               JOptionPane.showMessageDialog(frame, "Invalid quantity! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
           } catch (IllegalArgumentException ex) {
               JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           }
        });

        clearButton.addActionListener(_ -> {
            itemField.setText("");
            quantityField.setText("");
        });
    }
}
