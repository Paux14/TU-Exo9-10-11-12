package mediacity.exception;

public class OuvrageDejaDisponibleException extends RuntimeException {
    public OuvrageDejaDisponibleException(String titre) {
        super("L'ouvrage est déjà disponible, pas besoin de réserver : " + titre);
    }
}
