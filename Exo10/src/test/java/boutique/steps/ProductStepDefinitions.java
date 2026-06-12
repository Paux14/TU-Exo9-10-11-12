package boutique.steps;

import boutique.model.Product;
import boutique.repository.ProductRepository;
import boutique.service.ProductService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductStepDefinitions {

    private ProductRepository productRepository;
    private ProductService productService;
    private List<Product> lastResults;

    @Before
    public void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
        lastResults = null;
    }

    // ── US3 : Recherche de produits ───────────────────────────────────────────

    @Given("des produits correspondant au mot-clé {string}")
    public void produitsParMotCle(String keyword) {
        List<Product> produits = List.of(
                new Product("p1", "Clavier mécanique", "Informatique", 89.99),
                new Product("p2", "Clavier sans fil", "Informatique", 49.99)
        );
        when(productRepository.findByKeyword(keyword)).thenReturn(produits);
    }

    @When("l'utilisateur recherche {string}")
    public void rechercheMotCle(String keyword) {
        lastResults = productService.searchByKeyword(keyword);
    }

    @Then("l'utilisateur voit une liste de produits")
    public void voitListeProduits() {
        assertNotNull(lastResults);
        assertFalse(lastResults.isEmpty(), "La liste de produits ne doit pas être vide");
    }

    @Given("des produits disponibles à moins de {double} euros")
    public void produitsParPrix(double maxPrice) {
        List<Product> produits = List.of(
                new Product("p3", "Souris optique", "Informatique", 29.99),
                new Product("p4", "Tapis de souris", "Informatique", 14.99)
        );
        when(productRepository.findByMaxPrice(maxPrice)).thenReturn(produits);
    }

    @When("l'utilisateur recherche des produits avec un prix maximum de {double}")
    public void rechercheParPrix(double maxPrice) {
        lastResults = productService.searchByMaxPrice(maxPrice);
    }

    @Then("l'utilisateur voit une liste de produits filtrés par prix")
    public void voitListeFiltrePrix() {
        assertNotNull(lastResults);
        assertFalse(lastResults.isEmpty(), "La liste filtrée ne doit pas être vide");
        lastResults.forEach(p ->
                assertTrue(p.getPrice() <= 50.0, "Prix hors limite : " + p.getPrice())
        );
    }

    // ── US4 : Navigation par catégorie ────────────────────────────────────────

    @Given("des produits dans la catégorie {string}")
    public void produitsParCategorie(String category) {
        List<Product> produits = List.of(
                new Product("p5", "Smartphone X", category, 699.0),
                new Product("p6", "Tablette Y", category, 399.0)
        );
        when(productRepository.findByCategory(category)).thenReturn(produits);
    }

    @When("l'utilisateur sélectionne la catégorie {string}")
    public void selectionnerCategorie(String category) {
        lastResults = productService.browseByCategory(category);
    }

    @Then("l'utilisateur voit les produits de la catégorie {string}")
    public void voitProduitsCategorie(String category) {
        assertNotNull(lastResults);
        assertFalse(lastResults.isEmpty(), "Aucun produit trouvé pour la catégorie");
        lastResults.forEach(p ->
                assertEquals(category, p.getCategory(), "Mauvaise catégorie : " + p.getCategory())
        );
    }
}
