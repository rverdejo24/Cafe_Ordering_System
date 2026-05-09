package order;

public class Order {
    private int orderNumber = (int) (Math.random() * 1000);
    private double totalAmount;

    public double calculateTotalAmount(int qty, double price) {
        this.totalAmount = (double) qty * price;

        return totalAmount;
    }

    public void printReceipt() {
        System.out.println("Order #" + orderNumber + " - Total: ₱" + totalAmount);
    }
}
