package rooms.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import rooms.exception.RoomNotFoundException;
import rooms.exception.TimeSlotConflictException;
import rooms.model.Reservation;
import rooms.model.ReservationStatus;
import rooms.model.Room;
import rooms.repository.ReservationRepository;
import rooms.repository.RoomRepository;
import rooms.service.ReservationService;
import rooms.service.RoomService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationStepDefinitions {

    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private RoomService roomService;
    private ReservationService reservationService;

    private Room currentRoom;
    private Reservation lastReservation;
    private Exception lastException;

    @Before
    public void setUp() {
        roomRepository = new RoomRepository();
        reservationRepository = new ReservationRepository();
        roomService = new RoomService(roomRepository);
        reservationService = new ReservationService(reservationRepository, roomRepository);
        currentRoom = null;
        lastReservation = null;
        lastException = null;
    }

    @Given("une salle {string} de capacité {int} existe")
    public void roomExists(String name, int capacity) {
        currentRoom = roomService.create(name, capacity);
    }

    @Given("aucune salle n'existe")
    public void noRooms() {
    }

    @And("une réservation existe pour cette salle du {string} au {string}")
    public void existingReservation(String start, String end) {
        reservationService.create(
                currentRoom.getId(), "Occupant",
                LocalDateTime.parse(start), LocalDateTime.parse(end));
    }

    @When("je réserve cette salle pour {string} du {string} au {string}")
    public void makeReservation(String booker, String start, String end) {
        lastReservation = reservationService.create(
                currentRoom.getId(), booker,
                LocalDateTime.parse(start), LocalDateTime.parse(end));
    }

    @When("je tente de réserver la salle {string} pour {string} du {string} au {string}")
    public void attemptReservationUnknownRoom(String roomId, String booker, String start, String end) {
        try {
            reservationService.create(roomId, booker,
                    LocalDateTime.parse(start), LocalDateTime.parse(end));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("je tente de réserver cette salle pour {string} du {string} au {string}")
    public void attemptReservationConflict(String booker, String start, String end) {
        try {
            reservationService.create(currentRoom.getId(), booker,
                    LocalDateTime.parse(start), LocalDateTime.parse(end));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("la réservation est créée avec le statut {string}")
    public void reservationCreated(String status) {
        assertNotNull(lastReservation);
        assertEquals(ReservationStatus.valueOf(status), lastReservation.getStatus());
    }

    @Then("une erreur de salle introuvable est levée")
    public void roomNotFoundError() {
        assertNotNull(lastException, "Une exception était attendue");
        assertInstanceOf(RoomNotFoundException.class, lastException);
    }

    @Then("une erreur de conflit de créneau est levée")
    public void conflictError() {
        assertNotNull(lastException, "Une exception était attendue");
        assertInstanceOf(TimeSlotConflictException.class, lastException);
    }
}
