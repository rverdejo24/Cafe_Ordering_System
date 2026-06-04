import customer.Customer;
import order.Order;
import payment.Payment;

public class Main {
    static void main(String[] args) {
        Customer person1 = new Customer("Ann", "09194158866");
        person1.placeOrder();

        double total = Order.calculateTotalAmount(5, 10);
//        order1.printReceipt();

        double amountReceived = 100;
        Payment payment1 = new Payment("Cash", amountReceived);
        payment1.processPayment(total);
        payment1.displayPaymentInfo();
    }
}
