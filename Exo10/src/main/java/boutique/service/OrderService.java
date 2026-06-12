package boutique.service;

import boutique.model.Order;
import boutique.model.Product;
import boutique.repository.OrderRepository;
import boutique.repository.ProductRepository;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public String addProduct(String orderId, String productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Commande introuvable"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
        order.addProduct(product);
        orderRepository.save(order);
        return "Produit ajouté à la commande";
    }

    public String removeProduct(String orderId, String productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Commande introuvable"));
        order.removeProduct(productId);
        orderRepository.save(order);
        return "Produit retiré de la commande";
    }

    public String validateOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Commande introuvable"));
        order.validate();
        orderRepository.save(order);
        return "Commande validée";
    }
}
