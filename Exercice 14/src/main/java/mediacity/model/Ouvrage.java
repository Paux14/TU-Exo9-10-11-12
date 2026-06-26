package mediacity.model;

public class Ouvrage {

    private final String id;
    private final String titre;
    private final String auteur;
    private boolean disponible;

    public Ouvrage(String id, String titre, String auteur) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.disponible = true;
    }

    public String getId() { return id; }
    public String getTitre() { return titre; }
    public String getAuteur() { return auteur; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
