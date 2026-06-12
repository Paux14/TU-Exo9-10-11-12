package boutique.steps;

import boutique.model.Order;
import boutique.model.Product;
import boutique.repository.OrderRepository;
import boutique.repository.ProductRepository;
import boutique.service.OrderService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderStepDefinitions {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderService orderService;

    private Order currentOrder;
    private String lastMessage;
    private Exception lastException;

    @Before
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        orderService = new OrderService(orderRepository, productRepository);
        currentOrder = null;
        lastMessage = null;
        lastException = null;
    }

    // ── Given : état initial des commandes ───────────────────────────────────

    @Given("une commande {string} existe")
    public void commandeExiste(String orderId) {
        currentOrder = new Order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(currentOrder));
    }

    @Given("aucune commande {string} n'existe")
    public void commandeInexistante(String orderId) {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
    }

    @Given("une commande {string} contient le produit {string} en quantité {int}")
    public void commandeContientProduit(String orderId, String productId, int quantity) {
        Product product = new Product(productId, "Produit " + productId, "cat", 10.0);
        currentOrder = new Order(orderId);
        for (int i = 0; i < quantity; i++) {
            currentOrder.addProduct(product);
        }
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(currentOrder));
    }

    @Given("une commande {string} existe sans le produit {string}")
    public void commandeExisteSansProduit(String orderId, String productId) {
        currentOrder = new Order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(currentOrder));
    }

    @Given("un produit {string} existe")
    public void produitExiste(String productId) {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(new Product(productId, "Produit " + productId, "cat", 10.0)));
    }

    // ── US5 : Ajout de produit ────────────────────────────────────────────────

    @When("l'utilisateur ajoute le produit {string} à la commande {string}")
    public void ajouterProduit(String productId, String orderId) {
        lastMessage = orderService.addProduct(orderId, productId);
    }

    @Then("l'utilisateur reçoit une confirmation d'ajout")
    public void confirmationAjout() {
        assertNull(lastException, "Aucune exception ne devait être levée");
        assertNotNull(lastMessage);
        assertTrue(lastMessage.contains("ajouté"));
    }

    @Then("la quantité du produit {string} dans la commande est de {int}")
    public void quantiteProduit(String productId, int expected) {
        assertEquals(expected, currentOrder.getQuantityOf(productId),
                "Quantité incorrecte pour le produit " + productId);
    }

    @When("l'utilisateur tente d'ajouter le produit {string} à la commande {string}")
    public void tentativeAjout(String productId, String orderId) {
        try {
            orderService.addProduct(orderId, productId);
        } catch (Exception e) {
            lastException = e;
        }
    }

    // ── US6 : Suppression de produit ──────────────────────────────────────────

    @When("l'utilisateur supprime le produit {string} de la commande {string}")
    public void supprimerProduit(String productId, String orderId) {
        lastMessage = orderService.removeProduct(orderId, productId);
    }

    @Then("le produit {string} n'est plus dans la commande")
    public void produitPlusPresent(String productId) {
        assertFalse(currentOrder.containsProduct(productId),
                "Le produit " + productId + " devrait avoir été retiré");
    }

    @When("l'utilisateur tente de supprimer le produit {string} de la commande {string}")
    public void tentativeSuppression(String productId, String orderId) {
        try {
            orderService.removeProduct(orderId, productId);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("une erreur indique que le produit n'est pas dans la commande")
    public void erreurProduitAbsent() {
        assertNotNull(lastException, "Une exception était attendue");
        assertTrue(lastException.getMessage().contains("absent"),
                "Message inattendu : " + lastException.getMessage());
    }

    // ── US7 : Validation de commande ──────────────────────────────────────────

    @When("l'utilisateur valide la commande {string}")
    public void validerCommande(String orderId) {
        lastMessage = orderService.validateOrder(orderId);
    }

    @Then("l'utilisateur reçoit une confirmation de validation")
    public void confirmationValidation() {
        assertNull(lastException, "Aucune exception ne devait être levée");
        assertNotNull(lastMessage);
        assertTrue(lastMessage.contains("validée"));
    }

    @When("l'utilisateur tente de valider la commande {string}")
    public void tentativeValidation(String orderId) {
        try {
            orderService.validateOrder(orderId);
        } catch (Exception e) {
            lastException = e;
        }
    }

    // ── Shared error assertions ───────────────────────────────────────────────

    @Then("une erreur indique que la commande n'existe pas")
    public void erreurCommandeInexistante() {
        assertNotNull(lastException, "Une exception était attendue");
        assertTrue(lastException.getMessage().contains("introuvable"),
                "Message inattendu : " + lastException.getMessage());
    }
}
