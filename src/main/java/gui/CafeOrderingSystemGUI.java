package gui;

import order.Order;
import payment.Payment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class CafeOrderingSystemGUI {
    static void main(String[] args) {
        JFrame frame = new JFrame("Cafe Ordering System");
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(10);
        JLabel contactLabel = new JLabel("Contact Number:");
        JTextField contactField = new JTextField(10);
//        Restrict to numbers only
        contactField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        panel1.add(nameLabel);
        panel1.add(nameField);
        panel1.add(contactLabel);
        panel1.add(contactField);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel itemLabel = new JLabel("Item Name:");
        JTextField itemField = new JTextField(10);
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(5);
        panel2.add(itemLabel);
        panel2.add(itemField);
        panel2.add(quantityLabel);
        panel2.add(quantityField);

        JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        String[] paymentMethods = {"Cash", "G-Cash", "Card"};
        JComboBox<String> paymentMethodField = new JComboBox<>(paymentMethods);
        JLabel amountReceivedLabel = new JLabel("Amount Received:");
        JTextField amountReceivedField = new JTextField(10);
        panel3.add(paymentMethodLabel);
        panel3.add(paymentMethodField);
        panel3.add(amountReceivedLabel);
        panel3.add(amountReceivedField);

        JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add to Order");
        JButton clearButton = new JButton("Clear");
        panel4.add(addButton);
        panel4.add(clearButton);

        frame.setLayout(new GridLayout(4, 1));
        frame.add(panel1);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);


        frame.setSize(500,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        addButton.addActionListener(_ -> {
           try {
               String name = nameField.getText();
               String contact = contactField.getText();
               String item = itemField.getText().trim();
               String paymentMethod = Objects.requireNonNull(paymentMethodField.getSelectedItem()).toString();
               if (item.isEmpty()) {
                   throw new IllegalArgumentException("Please enter an item name.");
               }

               int quantity = Integer.parseInt(quantityField.getText().trim());
               if (quantity < 1) {
                   throw new IllegalArgumentException("Quantity must be greater than 0.");
               }

               double price = 150.00;
               double total = Order.calculateTotalAmount(quantity, price);

               double amountReceived = Integer.parseInt(amountReceivedField.getText().trim());
               Payment payment = new Payment(paymentMethod, amountReceived);
               payment.processPayment(total);

               JOptionPane.showMessageDialog(frame, "Name: " + name + "\nContact: " + contact + "\nItem: " + item + "\nQuantity: " + quantity + "\nTotal: ₱" + total + "\nPayment Method: " + payment.getPaymentMethod() + "\nAmount Received: " + payment.getAmountReceived() + "\nChange: " + payment.getChangeDue(), "Order Summary", JOptionPane.INFORMATION_MESSAGE);
           } catch (NumberFormatException ex) {
               JOptionPane.showMessageDialog(frame, "Invalid quantity! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
           } catch (IllegalArgumentException ex) {
               JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           }
        });

        clearButton.addActionListener(_ -> {
            nameField.setText("");
            contactField.setText("");
            itemField.setText("");
            quantityField.setText("");
            amountReceivedField.setText("");
        });
    }
}
