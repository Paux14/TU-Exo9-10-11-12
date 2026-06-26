package mediacity.service;

import mediacity.exception.AdherentSuspenduException;
import mediacity.exception.OuvrageDejaDisponibleException;
import mediacity.model.*;
import mediacity.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Ouvrage ouvrage;
    private Adherent adherent;
    private LocalDate aujourd_hui;

    @BeforeEach
    void setUp() {
        ouvrage = new Ouvrage("OUV-001", "Dune", "Herbert");
        ouvrage.setDisponible(false);
        adherent = new Adherent("ADH-001", "Bob");
        aujourd_hui = LocalDate.of(2025, 3, 1);
    }

    // --- Réservation réussie ---

    @Test
    void reserverOuvrage_ouvrageIndisponible_creeReservation() {
        // Arrange - ouvrage déjà indisponible

        // Act
        Reservation reservation = reservationService.reserverOuvrage(ouvrage, adherent, aujourd_hui);

        // Assert
        assertThat(reservation).isNotNull();
        assertThat(reservation.getOuvrage()).isEqualTo(ouvrage);
        assertThat(reservation.getAdherent()).isEqualTo(adherent);
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.EN_ATTENTE);
        verify(reservationRepository).save(reservation);
    }

    // --- Réservation refusée : ouvrage disponible ---

    @Test
    void reserverOuvrage_ouvrageDisponible_leveException() {
        // Arrange
        ouvrage.setDisponible(true);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOuvrage(ouvrage, adherent, aujourd_hui))
                .isInstanceOf(OuvrageDejaDisponibleException.class);
        verify(reservationRepository, never()).save(any());
    }

    // --- Réservation refusée : adhérent suspendu ---

    @Test
    void reserverOuvrage_adherentSuspendu_leveException() {
        // Arrange
        adherent.setSuspendu(true);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOuvrage(ouvrage, adherent, aujourd_hui))
                .isInstanceOf(AdherentSuspenduException.class);
        verify(reservationRepository, never()).save(any());
    }

    // --- Plusieurs réservations sur le même ouvrage ---

    @Test
    void listerReservationsEnAttente_retourneLaFileOrdonnee() {
        // Arrange
        Reservation r1 = new Reservation("R-001", ouvrage, adherent, aujourd_hui);
        Reservation r2 = new Reservation("R-002", ouvrage, new Adherent("ADH-002", "Carol"), aujourd_hui.plusDays(1));
        when(reservationRepository.findEnAttenteByOuvrageId("OUV-001")).thenReturn(List.of(r1, r2));

        // Act
        List<Reservation> liste = reservationService.listerReservationsEnAttente("OUV-001");

        // Assert
        assertThat(liste).hasSize(2);
        assertThat(liste.get(0).getAdherent().getNom()).isEqualTo("Bob");
        assertThat(liste.get(1).getAdherent().getNom()).isEqualTo("Carol");
    }

    // --- Notification du prochain réservant à la restitution ---

    @Test
    void notifierProchainReservant_passeLaPremierReservationADisponible() {
        // Arrange
        Reservation r1 = new Reservation("R-001", ouvrage, adherent, aujourd_hui);
        when(reservationRepository.findEnAttenteByOuvrageId("OUV-001")).thenReturn(List.of(r1));

        // Act
        reservationService.notifierProchainReservant(ouvrage);

        // Assert
        assertThat(r1.getStatut()).isEqualTo(StatutReservation.DISPONIBLE);
    }

    @Test
    void notifierProchainReservant_aucuneReservation_neFaitRien() {
        // Arrange
        when(reservationRepository.findEnAttenteByOuvrageId("OUV-001")).thenReturn(List.of());

        // Act & Assert
        assertThatCode(() -> reservationService.notifierProchainReservant(ouvrage))
                .doesNotThrowAnyException();
    }

    // --- Annulation de réservation ---

    @Test
    void annulerReservation_passeLaReservationAAnnulee() {
        // Arrange
        Reservation reservation = new Reservation("R-001", ouvrage, adherent, aujourd_hui);
        when(reservationRepository.findById("R-001")).thenReturn(Optional.of(reservation));

        // Act
        reservationService.annulerReservation("R-001");

        // Assert
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.ANNULEE);
    }
}
