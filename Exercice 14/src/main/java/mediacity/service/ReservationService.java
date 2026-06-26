package mediacity.service;

import mediacity.exception.AdherentSuspenduException;
import mediacity.exception.OuvrageDejaDisponibleException;
import mediacity.model.*;
import mediacity.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation reserverOuvrage(Ouvrage ouvrage, Adherent adherent, LocalDate dateReservation) {
        if (adherent.isSuspendu()) {
            throw new AdherentSuspenduException(adherent.getNom());
        }
        if (ouvrage.isDisponible()) {
            throw new OuvrageDejaDisponibleException(ouvrage.getTitre());
        }
        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(), ouvrage, adherent, dateReservation
        );
        reservationRepository.save(reservation);
        return reservation;
    }

    public List<Reservation> listerReservationsEnAttente(String ouvrageId) {
        return reservationRepository.findEnAttenteByOuvrageId(ouvrageId);
    }

    public void notifierProchainReservant(Ouvrage ouvrage) {
        List<Reservation> enAttente = reservationRepository.findEnAttenteByOuvrageId(ouvrage.getId());
        if (!enAttente.isEmpty()) {
            Reservation prochaine = enAttente.get(0);
            prochaine.setStatut(StatutReservation.DISPONIBLE);
        }
    }

    public void annulerReservation(String reservationId) {
        reservationRepository.findById(reservationId).ifPresent(r ->
                r.setStatut(StatutReservation.ANNULEE)
        );
    }
}
