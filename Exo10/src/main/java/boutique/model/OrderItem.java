package boutique.model;

public class OrderItem {
    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct()  { return product; }
    public int getQuantity()     { return quantity; }
    public void increment()      { this.quantity++; }
    public void decrement()      { this.quantity--; }
}
