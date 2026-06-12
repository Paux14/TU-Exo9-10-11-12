package boutique.repository;

import boutique.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findByKeyword(String keyword);
    List<Product> findByCategory(String category);
    List<Product> findByMaxPrice(double maxPrice);
    Optional<Product> findById(String id);
}
