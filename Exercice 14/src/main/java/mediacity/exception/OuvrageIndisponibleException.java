package mediacity.exception;

public class OuvrageIndisponibleException extends RuntimeException {
    public OuvrageIndisponibleException(String titre) {
        super("L'ouvrage est déjà emprunté : " + titre);
    }
}
