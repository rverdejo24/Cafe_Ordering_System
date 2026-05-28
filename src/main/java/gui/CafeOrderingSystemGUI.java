package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CafeOrderingSystemGUI {
    public static final Path ORDER_FILE = Paths.get("orders.csv");
    private static final String CSV_HEADER = "Item,Quantity,Price,Date";
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm");

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

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(5);

        // 3rd Row: Price Label
        mainPanelConstraints.gridx = 0;
        mainPanelConstraints.gridy = 2;
        mainPanelConstraints.fill = GridBagConstraints.NONE;
        mainPanelConstraints.weightx = 0;

        mainPanel.add(priceLabel, mainPanelConstraints);

        // 3rd Row: Price Field
        mainPanelConstraints.gridx = 1;
        mainPanelConstraints.gridy = 2;
        mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanelConstraints.weightx = 1;

        mainPanel.add(priceField, mainPanelConstraints);

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
                String quantityText = quantityField.getText().trim();
                String priceText = priceField.getText().trim();

                String errorMsg = validate(item, quantityText, priceText);
                if (!errorMsg.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int    quantity = Integer.parseInt(quantityText);
                double price    = Double.parseDouble(priceText);

                if (writeOrder(item, quantity, price)) {
                    JOptionPane.showMessageDialog(frame, "\"" + item + "\" added to order.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    itemField.setText("");
                    quantityField.setText("");
                    priceField.setText("");
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
            showOrdersTable(frame);
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
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(CSV_HEADER);
                    bw.newLine();
                }
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Could not initialize orders file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean writeOrder(String item, int quantity, double price) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDER_FILE.toFile(), true))) {
            String date = LocalDateTime.now().format(dateFormatter);
            String quotedItem = "\"" + item.replace("\"", "\"\"") + "\"";

            bw.write(quotedItem + "," + quantity + "," + String.format("%.2f", price) + "," + date);
            bw.newLine();

            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    private static void readOrders(DefaultTableModel tableModel, JLabel totalLabel) {
        tableModel.setRowCount(0); // clear before reload

        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_FILE.toFile()))) {
            String line;
            boolean firstLine = true;
            double grandTotal = 0;
            int count = 0;

            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                if (line.isBlank()) continue;

                String[] orderData = splitCsvLine(line);
                if (orderData.length < 4) continue; // skip malformed rows

                String item     = orderData[0];
                int quantity    = Integer.parseInt(orderData[1].trim());
                double price    = Double.parseDouble(orderData[2].trim());
                String date     = orderData[3].trim();
                double total    = quantity * price;

                tableModel.addRow(new Object[]{
                        item,
                        quantity,
                        String.format("₱%.2f", price),
                        String.format("₱%.2f", total),
                        date
                });

                grandTotal += total;
                count++;
            }

            totalLabel.setText(String.format("Grand Total: ₱%.2f   (%d order%s)",
                    grandTotal, count, count == 1 ? "" : "s"));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read orders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Validation
    private static String validate(String itemField, String quantityField, String priceField) {
        StringBuilder sb = new StringBuilder();

        if (itemField.isEmpty()) {
            sb.append("Please enter an item name.\n");
        }

        if (quantityField.isEmpty()) {
            sb.append("Please enter a quantity.\n");
        } else {
            try {
                if (Integer.parseInt(quantityField) <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                sb.append("Quantity must be a positive whole number.\n");
            }
        }

        if (priceField.isEmpty()) {
            sb.append("Please enter a price.\n");
        } else {
            try {
                if (Double.parseDouble(priceField) < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                sb.append("Price must be a non-negative number.\n");
            }
        }

        return sb.toString().trim();
    }

    // table window
    private static void showOrdersTable(JFrame parent) {
        JFrame tableFrame = new JFrame("Orders — Café Ordering System");

        String[] columns = {"Item", "Qty", "Unit Price", "Total", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(198, 228, 255));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Right-align numeric columns
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightAlign); // Qty
        table.getColumnModel().getColumn(2).setCellRenderer(rightAlign); // Unit Price
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlign); // Total

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Bottom bar
        JLabel totalLabel = new JLabel("Grand Total: ₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JButton refreshButton = new JButton("↻  Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.addActionListener(e -> readOrders(tableModel, totalLabel));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        bottomPanel.add(totalLabel,    BorderLayout.WEST);
        bottomPanel.add(refreshButton, BorderLayout.EAST);

        tableFrame.setLayout(new BorderLayout());
        tableFrame.add(scrollPane,  BorderLayout.CENTER);
        tableFrame.add(bottomPanel, BorderLayout.SOUTH);

        tableFrame.setSize(620, 380);
        tableFrame.setLocationRelativeTo(parent);
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load data immediately on open
        readOrders(tableModel, totalLabel);

        tableFrame.setVisible(true);
    }

    // CSV utility
    private static String[] splitCsvLine(String line) {
        java.util.List<String> tokens = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++; // escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}
