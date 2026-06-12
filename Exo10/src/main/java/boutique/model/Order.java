package boutique.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final List<OrderItem> items = new ArrayList<>();
    private boolean validated;

    public Order(String id) {
        this.id = id;
    }

    public String getId()            { return id; }
    public boolean isValidated()     { return validated; }
    public List<OrderItem> getItems(){ return items; }

    public void addProduct(Product product) {
        items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        OrderItem::increment,
                        () -> items.add(new OrderItem(product, 1))
                );
    }

    public void removeProduct(String productId) {
        OrderItem item = items.stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produit absent de la commande"));
        if (item.getQuantity() > 1) {
            item.decrement();
        } else {
            items.remove(item);
        }
    }

    public void validate() {
        this.validated = true;
    }

    public int getQuantityOf(String productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToInt(OrderItem::getQuantity)
                .findFirst()
                .orElse(0);
    }

    public boolean containsProduct(String productId) {
        return items.stream().anyMatch(item -> item.getProduct().getId().equals(productId));
    }
}
