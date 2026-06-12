package rooms.exception;

public class TimeSlotConflictException extends RuntimeException {
    public TimeSlotConflictException() {
        super("Le créneau chevauche une réservation existante");
    }
}
