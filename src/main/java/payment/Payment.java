package payment;

public class Payment {
    private String paymentMethod;
    private double amountReceived;
    private double changeDue;

    public Payment(String paymentMethod, double amountReceived) {
        this.paymentMethod = paymentMethod;
        this.amountReceived = amountReceived;
    }

    public void processPayment(double total) {
        changeDue = amountReceived - total;
    }

    public void displayPaymentInfo() {
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Amount Received: ₱" + amountReceived);
        System.out.println("Change Due: ₱" + changeDue);
    }
}
