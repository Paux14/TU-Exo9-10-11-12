package mediacity.exception;

public class PretIntrouvableException extends RuntimeException {
    public PretIntrouvableException(String id) {
        super("Prêt introuvable : " + id);
    }
}
