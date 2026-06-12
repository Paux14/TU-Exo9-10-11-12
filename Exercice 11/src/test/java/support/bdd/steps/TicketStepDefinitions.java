package support.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import support.exception.InvalidStatusTransitionException;
import support.exception.TicketNotFoundException;
import support.model.Priority;
import support.model.Status;
import support.model.Ticket;
import support.repository.TicketRepository;
import support.service.TicketService;

import static org.junit.jupiter.api.Assertions.*;

public class TicketStepDefinitions {

    private TicketRepository repository;
    private TicketService service;
    private Ticket lastTicket;
    private Exception lastException;

    @Before
    public void setUp() {
        repository = new TicketRepository();
        service = new TicketService(repository);
        lastTicket = null;
        lastException = null;
    }

    @Given("aucun ticket n'existe")
    public void noTicketsExist() {
        // repository is fresh — rien à faire
    }

    @Given("un ticket {string} avec la priorité {string} a été créé")
    public void ticketCreated(String title, String priority) {
        lastTicket = service.create(title, Priority.valueOf(priority));
    }

    @And("le ticket a été résolu")
    public void ticketIsResolved() {
        lastTicket = service.updateStatus(lastTicket.getId(), Status.RESOLVED);
    }

    @When("je crée un ticket avec le titre {string} et la priorité {string}")
    public void createTicket(String title, String priority) {
        lastTicket = service.create(title, Priority.valueOf(priority));
    }

    @When("je modifie le statut du ticket à {string}")
    public void updateStatus(String status) {
        lastTicket = service.updateStatus(lastTicket.getId(), Status.valueOf(status));
    }

    @When("je tente de modifier le statut du ticket à {string}")
    public void attemptUpdateStatus(String status) {
        try {
            service.updateStatus(lastTicket.getId(), Status.valueOf(status));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("je consulte le ticket avec l'identifiant {string}")
    public void getTicket(String id) {
        try {
            lastTicket = service.findById(id);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("le ticket est créé avec le statut {string}")
    public void ticketHasStatus(String status) {
        assertNotNull(lastTicket);
        assertEquals(Status.valueOf(status), lastTicket.getStatus());
    }

    @Then("le ticket possède un identifiant")
    public void ticketHasId() {
        assertNotNull(lastTicket.getId());
        assertFalse(lastTicket.getId().isBlank());
    }

    @Then("le statut du ticket est {string}")
    public void statusIs(String status) {
        assertEquals(Status.valueOf(status), lastTicket.getStatus());
    }

    @Then("une erreur de transition invalide est levée")
    public void invalidTransitionError() {
        assertNotNull(lastException, "Une exception était attendue");
        assertInstanceOf(InvalidStatusTransitionException.class, lastException);
    }

    @Then("une erreur de ticket introuvable est levée")
    public void ticketNotFoundError() {
        assertNotNull(lastException, "Une exception était attendue");
        assertInstanceOf(TicketNotFoundException.class, lastException);
    }
}
