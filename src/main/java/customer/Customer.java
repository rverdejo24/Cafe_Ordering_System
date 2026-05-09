package customer;

public class Customer {
    private String name;
    private String contactNum;

    public Customer(String name, String contactNum) {
        this.name = name;
        this.contactNum = contactNum;
    }

    public void placeOrder() {
        System.out.println(name + " placed an order.");
    }
}
