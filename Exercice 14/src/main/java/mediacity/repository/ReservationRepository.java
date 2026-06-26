package mediacity.repository;

import mediacity.model.Reservation;
import mediacity.model.StatutReservation;

import java.util.*;
import java.util.stream.Collectors;

public class ReservationRepository {

    private final Map<String, Reservation> store = new HashMap<>();

    public void save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
    }

    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Reservation> findByOuvrageId(String ouvrageId) {
        return store.values().stream()
                .filter(r -> r.getOuvrage().getId().equals(ouvrageId))
                .collect(Collectors.toList());
    }

    public List<Reservation> findEnAttenteByOuvrageId(String ouvrageId) {
        return store.values().stream()
                .filter(r -> r.getOuvrage().getId().equals(ouvrageId)
                        && r.getStatut() == StatutReservation.EN_ATTENTE)
                .sorted(Comparator.comparing(Reservation::getDateReservation))
                .collect(Collectors.toList());
    }
}
