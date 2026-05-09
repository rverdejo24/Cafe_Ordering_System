package order;

public class Order {
    private int orderNumber = (int) (Math.random() * 1000);


    public static double calculateTotalAmount(int qty, double price) {
        return (double) qty * price;
    }

    /*public void printReceipt() {
        System.out.println("Order #" + orderNumber + " - Total: ₱" + totalAmount);
    }*/
}
