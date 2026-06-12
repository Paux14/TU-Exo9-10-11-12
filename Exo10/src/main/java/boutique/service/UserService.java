package boutique.service;

import boutique.model.User;
import boutique.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createAccount(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Un compte avec ce nom d'utilisateur existe déjà");
        }
        userRepository.save(new User(username, email, password));
        return "Compte créé avec succès";
    }

    public String login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .map(u -> "Connexion réussie")
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));
    }
}
