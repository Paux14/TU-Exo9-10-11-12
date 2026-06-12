package rooms.exception;

public class InvalidTimeSlotException extends RuntimeException {
    public InvalidTimeSlotException() {
        super("La date de fin doit être strictement après la date de début");
    }
}
