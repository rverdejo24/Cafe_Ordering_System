package gui;

import order.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class CafeOrderingSystemGUI {
    public static final Path ORDER_FILE = Paths.get("orders.csv");
    private static final String CSV_HEADER = "Item,Quantity,Price,Date";
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm");
    public static ArrayList<String> menuItems = new ArrayList<>();
    public static HashSet<String> menuSet = new HashSet<>();

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        JFrame frame = new JFrame("Café Ordering System");

        loadMenu(frame, menuItems, menuSet);

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
        JButton deleteButton = new JButton("Delete Menu");
        JButton clearButton = new JButton("Clear");
        JButton viewMenuButton = new JButton("View Menu");
        JButton viewOrderButton = new JButton("View Orders");
        JButton computeSummaryButton = new JButton("Compute Sales");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(viewMenuButton);
        buttonPanel.add(viewOrderButton);
        buttonPanel.add(computeSummaryButton);

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(mainPanel);
        frame.add(buttonPanel);

        // --- Step 1: Create the orders.txt file when program runs ---
        initOrderFile(frame);

        // Add button
        addButton.addActionListener(_ -> {
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

            addMenuItem(frame, menuItems, menuSet, item);
            saveMenuToCSV(menuItems);

            if (writeOrder(item, quantity, price)) {
                JOptionPane.showMessageDialog(frame, "\"" + item + "\" added to order.", "Success", JOptionPane.INFORMATION_MESSAGE);
                itemField.setText("");
                quantityField.setText("");
                priceField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to save order.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete Button
        deleteButton.addActionListener(_ -> {
            String item = itemField.getText().trim();

            deleteMenuItem(frame, menuItems, menuSet, item);
            saveMenuToCSV(menuItems);
        });

        // Clear button
        clearButton.addActionListener(_ -> {
            itemField.setText("");
            quantityField.setText("");
        });

        // View menu
        viewMenuButton.addActionListener(_ -> displayMenu(frame, menuItems));

        // View order button
        viewOrderButton.addActionListener(_ -> displayOrders(frame));

        // Compute Sales
        computeSummaryButton.addActionListener( _ -> {
            List<Double> totals = new ArrayList<>();
            List<Integer> totalQuantity = new ArrayList<>();
            List<Order> orders = readOrders();

            if (validateIfEmpty(orders)) {
                return;
            }

            try {
                for (Order order : orders) {
                    totals.add(order.total());
                    totalQuantity.add(order.quantity());
                }

                int quantitySummary = computeTotalQuantity(totalQuantity);
                double totalSales = computeTotalSales(totals);
                double averageSale = computeAverageSale(totals);
                double discount = computeDiscount(totalSales);
                double finalAmount = totalSales - discount;


                JTextArea output = new JTextArea();
                output.append("=== Café Sales Summary ===\n");
                for (Order order : orders) {
                    output.append(order.item() + " x " + order.quantity() + " = ₱" + order.total() + "\n");
                }
                output.append("--------------------------------\n");
                output.append("Total Quantity: " + quantitySummary + "\n");
                output.append("Total Sales: ₱" + totalSales + "\n");
                output.append("Average Sale: ₱" + averageSale + "\n");
                output.append("Discount: ₱" + discount + "\n");
                output.append("Final Amount: ₱" + finalAmount + "\n\n");

                JOptionPane.showMessageDialog(
                        null, new JScrollPane(output),
                        "Sales Computation", JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "No items in the list.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null, "Error reading file: " + e.getMessage()
                );
            }
        });

        frame.setSize(800, 200);
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
                System.out.println("Loading data from csv file.");
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

    private static List<Order> readOrders() {
        List<Order> orders = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_FILE.toFile()))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                } // skip header

                if (line.isBlank()) continue;

                String[] orderData = splitCsvLine(line);
                if (orderData.length < 4) continue; // skip malformed rows

                int orderNumber = (int) (Math.random() * 1000000);
                String item     = orderData[0];
                int quantity    = Integer.parseInt(orderData[1].trim());
                double price    = Double.parseDouble(orderData[2].trim());
                double total    = Order.calculateTotalAmount(quantity, price);
                String date     = orderData[3].trim();

                Order order = new Order(orderNumber, item, quantity, price, total, date);
                orders.add(order);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read orders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return orders;
    }

    private static void updateOrder(
            int rowIndex,
            String newItem,
            int newQuantity,
            double newPrice) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(ORDER_FILE.toFile()))) {

            String line;
            int currentRow = -1;

            while ((line = br.readLine()) != null) {

                if (currentRow == -1) { // header
                    lines.add(line);
                    currentRow++;
                    continue;
                }

                if (currentRow == rowIndex) {

                    String[] data = splitCsvLine(line);

                    String updatedLine =
                            "\"" + newItem.replace("\"", "\"\"") + "\"" +
                                    "," + newQuantity +
                                    "," + String.format("%.2f", newPrice) +
                                    "," + data[3];

                    lines.add(updatedLine);
                } else {
                    lines.add(line);
                }

                currentRow++;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to update order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(ORDER_FILE.toFile()))) {

            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to save updated order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Add to table
    private static void addOrdersToTable(DefaultTableModel tableModel, JLabel totalLabel) {
        tableModel.setRowCount(0);
        int count = 0;
        double grandTotal = 0;

        List<Order> orders = readOrders();

        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                    order.item(),
                    order.quantity(),
                    String.format("₱%.2f", order.price()),
                    String.format("₱%.2f", Order.calculateTotalAmount(order.quantity(), order.price())),
                    order.date()
            });

            grandTotal += Order.calculateTotalAmount(order.quantity(), order.price());
            count++;
        }

        totalLabel.setText(String.format("Grand Total: ₱%.2f   (%d order%s)",
                grandTotal, count, count == 1 ? "" : "s"));
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

    private static <T> boolean validateIfEmpty(List<T> arrays) {
        if (arrays.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items in the list.", "Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        return false;
    }

    // menu window
    public static void displayMenu(JFrame frame, ArrayList<String> menuItems) {
        if (menuItems.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No menu items available.", "Café Menu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Join items into a formatted string with line breaks
        String menuDisplay = String.join("\n• ", menuItems);
        JOptionPane.showMessageDialog(frame, "Current Café Menu:\n• " + menuDisplay, "Café Menu", JOptionPane.INFORMATION_MESSAGE);
    }

    // order window
    private static void displayOrders(JFrame parent) {
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
        table.getColumnModel().getColumn(4).setCellRenderer(rightAlign); // Date

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

        JButton generateSummaryButton = new JButton("Generate Summary");
        generateSummaryButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        generateSummaryButton.addActionListener(_ -> {});

        JButton updateButton = new JButton("Update Order");
        updateButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        updateButton.addActionListener(_ -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        tableFrame,
                        "Please select an order to update.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JTextField itemField = new JTextField(
                    table.getValueAt(selectedRow, 0).toString()
            );

            JTextField quantityField = new JTextField(
                    table.getValueAt(selectedRow, 1).toString()
            );

            JTextField priceField = new JTextField(
                    table.getValueAt(selectedRow, 2)
                            .toString()
                            .replace("₱", "")
            );

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

            panel.add(new JLabel("Item Name:"));
            panel.add(itemField);

            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);

            panel.add(new JLabel("Price:"));
            panel.add(priceField);

            int result = JOptionPane.showConfirmDialog(
                    tableFrame,
                    panel,
                    "Update Order",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String item = itemField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            String priceStr = priceField.getText().trim();

            String error = validate(item, quantityStr, priceStr);

            if (!error.isEmpty()) {
                JOptionPane.showMessageDialog(
                        tableFrame,
                        error,
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            updateOrder(
                    selectedRow,
                    item,
                    Integer.parseInt(quantityStr),
                    Double.parseDouble(priceStr)
            );

            addOrdersToTable(tableModel, totalLabel);

            JOptionPane.showMessageDialog(
                    tableFrame,
                    "Order updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JButton deleteButton = new JButton("Delete Order");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.addActionListener(_ -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        tableFrame,
                        "Please select an order to delete.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    tableFrame,
                    "Delete selected order?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            deleteOrder(selectedRow);

            addOrdersToTable(tableModel, totalLabel);

            JOptionPane.showMessageDialog(
                    tableFrame,
                    "Order deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JButton refreshButton = new JButton("↻  Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.addActionListener(_ -> addOrdersToTable(tableModel, totalLabel));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        bottomPanel.add(totalLabel,    BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        tableFrame.setLayout(new BorderLayout());
        tableFrame.add(scrollPane,  BorderLayout.CENTER);
        tableFrame.add(bottomPanel, BorderLayout.SOUTH);

        tableFrame.setSize(620, 380);
        tableFrame.setLocationRelativeTo(parent);
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load data immediately on open
        addOrdersToTable(tableModel, totalLabel);

        tableFrame.setVisible(true);
    }

    // CSV utility
    private static String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
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

    // Computation logic
    // Adds up all order totals
    private static double computeTotalSales(List<Double> totals) {
        double total = 0;
        for (double t : totals)
            total += t;
        return total;
    }

    // Count the total quantity of orders
    private static int computeTotalQuantity(List<Integer> quantity) {
        int totalQuantity = 0;

        for (Integer item : quantity) {
            totalQuantity += item;
        }

        return totalQuantity;
    }

    // Calculates average sale value
    private static double computeAverageSale(List<Double> totals) {
        return computeTotalSales(totals) / totals.size();
    }

    // Applies a 10% discount if total sales exceed ₱1000
    private static double computeDiscount(double totalSales) {
        if (totalSales >= 1000)
            return totalSales * 0.10; // 10% discount
        else
            return 0;
    }

    public static void loadMenu(JFrame frame, ArrayList<String> menuItems, HashSet<String> menuSet) {
        File file = new File("menu.csv");

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    JOptionPane.showMessageDialog(frame,
                            "menu.csv not found. A new empty menu file was created.",
                            "Café Menu",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame,
                        "Failed to create menu file: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader("menu.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String item = line.trim();
                menuItems.add(item);
                menuSet.add(item);
            }
            JOptionPane.showMessageDialog(frame, "Menu loaded successfully!",
                    "Café Menu", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading menu: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void addMenuItem(JFrame frame, ArrayList<String> menuItems, HashSet<String> menuSet, String newItem) {
        if (menuSet.contains(newItem)) {
            JOptionPane.showMessageDialog(frame,
                    "Item already exists!",
                    "Duplicate Entry",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            menuItems.add(newItem);
            menuSet.add(newItem);
            JOptionPane.showMessageDialog(frame,
                    newItem + " added successfully!",
                    "Item Added",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void saveMenuToCSV(ArrayList<String> menuItems) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("menu.csv"))) {
            for (String item : menuItems) {
                pw.println(item);
            }
            System.out.println("Menu saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static void deleteMenuItem(JFrame frame, ArrayList<String> menuItems, HashSet<String> menuSet, String itemToDelete) {
        if (menuSet.contains(itemToDelete)) {
            menuItems.remove(itemToDelete);
            menuSet.remove(itemToDelete);
            JOptionPane.showMessageDialog(frame,
                    itemToDelete + " deleted successfully!",
                    "Item Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Item not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void deleteOrder(int index) {
        File file = new File("orders.csv");

        if (!file.exists()) {
            return;
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            return;
        }

        int csvIndex = index + 1;

        // remove selected row
        if (csvIndex >= 1 && index < lines.size()) {
            lines.remove(csvIndex);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
