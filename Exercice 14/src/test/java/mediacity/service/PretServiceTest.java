package mediacity.service;

import mediacity.exception.AdherentSuspenduException;
import mediacity.exception.OuvrageIndisponibleException;
import mediacity.exception.PretIntrouvableException;
import mediacity.model.*;
import mediacity.repository.PretRepository;
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
class PretServiceTest {

    @Mock
    private PretRepository pretRepository;

    @InjectMocks
    private PretService pretService;

    private Ouvrage ouvrage;
    private Adherent adherent;
    private LocalDate aujourd_hui;

    @BeforeEach
    void setUp() {
        ouvrage = new Ouvrage("OUV-001", "Le Petit Prince", "Saint-Exupéry");
        adherent = new Adherent("ADH-001", "Alice");
        aujourd_hui = LocalDate.of(2025, 1, 15);
    }

    // --- Création de prêt ---

    @Test
    void creerPret_ouvrageDisponible_creeLePreet() {
        // Arrange - ouvrage disponible, adherent actif
        // Act
        Pret pret = pretService.creerPret(ouvrage, adherent, aujourd_hui);

        // Assert
        assertThat(pret).isNotNull();
        assertThat(pret.getOuvrage()).isEqualTo(ouvrage);
        assertThat(pret.getAdherent()).isEqualTo(adherent);
        assertThat(pret.getStatut()).isEqualTo(StatutPret.EN_COURS);
        assertThat(pret.getDateRetourPrevue()).isEqualTo(aujourd_hui.plusDays(21));
        assertThat(ouvrage.isDisponible()).isFalse();
        verify(pretRepository).save(pret);
    }

    @Test
    void creerPret_ouvrageIndisponible_leveException() {
        // Arrange
        ouvrage.setDisponible(false);

        // Act & Assert
        assertThatThrownBy(() -> pretService.creerPret(ouvrage, adherent, aujourd_hui))
                .isInstanceOf(OuvrageIndisponibleException.class);
        verify(pretRepository, never()).save(any());
    }

    @Test
    void creerPret_adherentSuspendu_leveException() {
        // Arrange
        adherent.setSuspendu(true);

        // Act & Assert
        assertThatThrownBy(() -> pretService.creerPret(ouvrage, adherent, aujourd_hui))
                .isInstanceOf(AdherentSuspenduException.class);
        verify(pretRepository, never()).save(any());
    }

    // --- Retour sans retard ---

    @Test
    void retournerOuvrage_dansLesDelais_penaliteNulle() {
        // Arrange
        Pret pret = new Pret("PRET-001", ouvrage, adherent, aujourd_hui);
        LocalDate dateRetour = aujourd_hui.plusDays(10);
        when(pretRepository.findById("PRET-001")).thenReturn(Optional.of(pret));

        // Act
        double penalite = pretService.retournerOuvrage("PRET-001", dateRetour);

        // Assert
        assertThat(penalite).isEqualTo(0.0);
        assertThat(pret.getStatut()).isEqualTo(StatutPret.RENDU);
        assertThat(ouvrage.isDisponible()).isTrue();
        assertThat(adherent.getNombreRetardsAnnee()).isEqualTo(0);
    }

    // --- Retour avec retard ---

    @Test
    void retournerOuvrage_avecRetard_calculePenalite() {
        // Arrange
        Pret pret = new Pret("PRET-001", ouvrage, adherent, aujourd_hui);
        LocalDate dateRetour = aujourd_hui.plusDays(25); // 4 jours de retard

        when(pretRepository.findById("PRET-001")).thenReturn(Optional.of(pret));

        // Act
        double penalite = pretService.retournerOuvrage("PRET-001", dateRetour);

        // Assert
        assertThat(penalite).isEqualTo(4 * 0.15);
        assertThat(pret.getStatut()).isEqualTo(StatutPret.EN_RETARD);
        assertThat(ouvrage.isDisponible()).isTrue();
        assertThat(adherent.getNombreRetardsAnnee()).isEqualTo(1);
    }

    @Test
    void retournerOuvrage_pretIntrouvable_leveException() {
        // Arrange
        when(pretRepository.findById("INCONNU")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pretService.retournerOuvrage("INCONNU", aujourd_hui))
                .isInstanceOf(PretIntrouvableException.class);
    }

    // --- Suspension après 3 retards ---

    @Test
    void retournerOuvrage_troisiemeRetard_suspendLadherent() {
        // Arrange
        adherent.setNombreRetardsAnnee(2);
        Pret pret = new Pret("PRET-001", ouvrage, adherent, aujourd_hui);
        LocalDate dateRetour = aujourd_hui.plusDays(30); // retard

        when(pretRepository.findById("PRET-001")).thenReturn(Optional.of(pret));

        // Act
        pretService.retournerOuvrage("PRET-001", dateRetour);

        // Assert
        assertThat(adherent.getNombreRetardsAnnee()).isEqualTo(3);
        assertThat(adherent.isSuspendu()).isTrue();
    }

    @Test
    void creerPret_adherentSuspenduApres3Retards_nePouvantPlusEmprunter() {
        // Arrange
        adherent.setNombreRetardsAnnee(2);
        Pret pret1 = new Pret("PRET-001", ouvrage, adherent, aujourd_hui);
        LocalDate dateRetard = aujourd_hui.plusDays(30);
        when(pretRepository.findById("PRET-001")).thenReturn(Optional.of(pret1));
        pretService.retournerOuvrage("PRET-001", dateRetard);

        Ouvrage ouvrage2 = new Ouvrage("OUV-002", "1984", "Orwell");

        // Act & Assert
        assertThat(adherent.isSuspendu()).isTrue();
        assertThatThrownBy(() -> pretService.creerPret(ouvrage2, adherent, aujourd_hui.plusDays(31)))
                .isInstanceOf(AdherentSuspenduException.class);
    }

    // --- Liste des prêts ---

    @Test
    void listerPrets_retourneTousLesPrets() {
        // Arrange
        Pret pret = new Pret("PRET-001", ouvrage, adherent, aujourd_hui);
        when(pretRepository.findAll()).thenReturn(List.of(pret));

        // Act
        List<Pret> prets = pretService.listerPrets();

        // Assert
        assertThat(prets).hasSize(1).contains(pret);
    }
}
