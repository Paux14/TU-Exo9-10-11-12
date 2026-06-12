package boutique.steps;

import boutique.model.User;
import boutique.repository.UserRepository;
import boutique.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountStepDefinitions {

    private UserRepository userRepository;
    private UserService userService;
    private String lastMessage;
    private Exception lastException;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
        lastMessage = null;
        lastException = null;
    }

    // ── US1 : Création de compte ──────────────────────────────────────────────

    @Given("un formulaire d'inscription est disponible")
    public void formulaireDisponible() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
    }

    @When("l'utilisateur s'inscrit avec l'email {string}, le nom {string} et le mot de passe {string}")
    public void inscription(String email, String username, String password) {
        lastMessage = userService.createAccount(username, email, password);
    }

    @Then("l'utilisateur reçoit une confirmation d'inscription")
    public void confirmationInscription() {
        assertNotNull(lastMessage);
        assertTrue(lastMessage.contains("succès"), "Message attendu : " + lastMessage);
    }

    @Given("un utilisateur {string} est déjà enregistré")
    public void utilisateurDejaEnregistre(String username) {
        when(userRepository.existsByUsername(username)).thenReturn(true);
    }

    @When("l'utilisateur tente de s'inscrire avec le nom {string}")
    public void tentativeInscription(String username) {
        try {
            userService.createAccount(username, "test@example.com", "pass");
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("une erreur indique que le compte existe déjà")
    public void erreurCompteExistant() {
        assertNotNull(lastException, "Une exception était attendue");
        assertTrue(lastException.getMessage().contains("existe déjà"));
    }

    // ── US2 : Connexion ───────────────────────────────────────────────────────

    @Given("un utilisateur {string} existe avec le mot de passe {string}")
    public void utilisateurExiste(String username, String password) {
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(new User(username, "user@example.com", password)));
    }

    @When("l'utilisateur se connecte avec {string} et {string}")
    public void connexion(String username, String password) {
        try {
            lastMessage = userService.login(username, password);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("l'utilisateur est redirigé vers la page d'accueil")
    public void redirectionAccueil() {
        assertNull(lastException, "Aucune exception ne devait être levée");
        assertNotNull(lastMessage);
        assertTrue(lastMessage.contains("réussie"));
    }

    @Then("l'utilisateur voit un message d'erreur de connexion")
    public void erreurConnexion() {
        assertNotNull(lastException, "Une exception de connexion était attendue");
    }
}
