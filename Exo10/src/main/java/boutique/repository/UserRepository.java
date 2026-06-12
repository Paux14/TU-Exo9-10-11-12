package boutique.repository;

import boutique.model.User;
import java.util.Optional;

public interface UserRepository {
    boolean existsByUsername(String username);
    void save(User user);
    Optional<User> findByUsername(String username);
}
