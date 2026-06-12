package rooms.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String id) {
        super("Salle introuvable : " + id);
    }
}
