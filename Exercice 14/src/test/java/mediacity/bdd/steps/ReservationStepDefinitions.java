package mediacity.bdd.steps;

import mediacity.exception.AdherentSuspenduException;
import mediacity.exception.OuvrageDejaDisponibleException;
import mediacity.model.*;
import mediacity.repository.PretRepository;
import mediacity.repository.ReservationRepository;
import mediacity.service.PretService;
import mediacity.service.ReservationService;
import io.cucumber.java.Before;
import io.cucumber.java.fr.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationStepDefinitions {

    private PretService pretService;
    private ReservationService reservationService;

    private final Map<String, Ouvrage> ouvrages = new HashMap<>();
    private final Map<String, Adherent> adherents = new HashMap<>();
    private final Map<String, Reservation> reservations = new HashMap<>();

    private Exception exceptionLevee;

    @Before
    public void setUp() {
        PretRepository pretRepository = new PretRepository();
        ReservationRepository reservationRepository = new ReservationRepository();
        pretService = new PretService(pretRepository);
        reservationService = new ReservationService(reservationRepository);
    }

    @Etantdonné("un ouvrage {string} actuellement emprunté")
    public void ouvrageEmprunte(String titre) {
        Ouvrage ouvrage = new Ouvrage(titre, titre, "Auteur");
        ouvrage.setDisponible(false);
        ouvrages.put(titre, ouvrage);
    }

    @Etantdonné("un ouvrage {string} disponible en rayon")
    public void ouvrageDisponible(String titre) {
        Ouvrage ouvrage = new Ouvrage(titre, titre, "Auteur");
        ouvrages.put(titre, ouvrage);
    }

    @Etantdonné("un adhérent actif {string}")
    public void adherentActif(String nom) {
        adherents.put(nom, new Adherent(nom, nom));
    }

    @Etantdonné("un adhérent suspendu {string}")
    public void adherentSuspendu(String nom) {
        Adherent adherent = new Adherent(nom, nom);
        adherent.setSuspendu(true);
        adherents.put(nom, adherent);
    }

    @Etantdonné("un adhérent actif {string} a réservé l'ouvrage {string}")
    public void adherentAReserve(String nom, String titre) {
        adherentActif(nom);
        Adherent adherent = adherents.get(nom);
        Ouvrage ouvrage = ouvrages.get(titre);
        Reservation reservation = reservationService.reserverOuvrage(ouvrage, adherent, LocalDate.now());
        reservations.put(nom, reservation);
    }

    @Quand("{string} réserve l'ouvrage {string}")
    public void adherentReserve(String nom, String titre) {
        Adherent adherent = adherents.get(nom);
        Ouvrage ouvrage = ouvrages.get(titre);
        Reservation reservation = reservationService.reserverOuvrage(ouvrage, adherent, LocalDate.now());
        reservations.put(nom, reservation);
    }

    @Quand("{string} tente de réserver l'ouvrage {string}")
    public void adherentTenteReserver(String nom, String titre) {
        try {
            Adherent adherent = adherents.get(nom);
            Ouvrage ouvrage = ouvrages.get(titre);
            reservationService.reserverOuvrage(ouvrage, adherent, LocalDate.now());
        } catch (Exception e) {
            exceptionLevee = e;
        }
    }

    @Quand("l'ouvrage {string} est restitué")
    public void ouvrageRestitue(String titre) {
        Ouvrage ouvrage = ouvrages.get(titre);
        ouvrage.setDisponible(true);
        reservationService.notifierProchainReservant(ouvrage);
    }

    @Alors("la réservation est enregistrée avec le statut {string}")
    public void reservationStatut(String statut) {
        Reservation reservation = reservations.values().iterator().next();
        assertThat(reservation.getStatut().name()).isEqualTo(statut);
    }

    @Alors("il y a {int} réservations en attente pour {string}")
    public void nombreReservationsEnAttente(int nombre, String titre) {
        List<Reservation> liste = reservationService.listerReservationsEnAttente(titre);
        assertThat(liste).hasSize(nombre);
    }

    @Alors("la réservation de {string} passe au statut {string}")
    public void reservationDeAdherentStatut(String nom, String statut) {
        Reservation reservation = reservations.get(nom);
        assertThat(reservation.getStatut().name()).isEqualTo(statut);
    }

    @Alors("une erreur d'adhérent suspendu est levée")
    public void erreurAdherentSuspendu() {
        assertThat(exceptionLevee).isInstanceOf(AdherentSuspenduException.class);
    }

    @Alors("une erreur indique que l'ouvrage est déjà disponible")
    public void erreurOuvrageDisponible() {
        assertThat(exceptionLevee).isInstanceOf(OuvrageDejaDisponibleException.class);
    }
}
