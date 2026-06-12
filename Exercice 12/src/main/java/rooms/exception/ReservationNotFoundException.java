package rooms.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String id) {
        super("Réservation introuvable : " + id);
    }
}
