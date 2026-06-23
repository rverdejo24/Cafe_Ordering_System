package order;

import java.time.LocalDateTime;
import java.util.Objects;

public record Order (int orderNumber, String item, int quantity, double price, double total, String date) {

    public Order {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(quantity, "quantity is null");
        Objects.requireNonNull(price, "price is null");
        Objects.requireNonNull(date, "date is null");
    }

    public static double calculateTotalAmount(int qty, double price) {
        return (double) qty * price;
    }
}
