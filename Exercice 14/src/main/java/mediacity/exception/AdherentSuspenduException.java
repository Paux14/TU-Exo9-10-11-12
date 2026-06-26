package mediacity.exception;

public class AdherentSuspenduException extends RuntimeException {
    public AdherentSuspenduException(String nom) {
        super("L'adhérent est suspendu et ne peut pas emprunter : " + nom);
    }
}
