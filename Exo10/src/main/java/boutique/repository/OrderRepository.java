package boutique.repository;

import boutique.model.Order;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String orderId);
    void save(Order order);
}
