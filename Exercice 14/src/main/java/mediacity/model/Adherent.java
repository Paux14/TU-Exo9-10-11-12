package mediacity.model;

public class Adherent {

    private final String id;
    private final String nom;
    private int nombreRetardsAnnee;
    private boolean suspendu;

    public Adherent(String id, String nom) {
        this.id = id;
        this.nom = nom;
        this.nombreRetardsAnnee = 0;
        this.suspendu = false;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public int getNombreRetardsAnnee() { return nombreRetardsAnnee; }
    public boolean isSuspendu() { return suspendu; }

    public void incrementerRetards() {
        this.nombreRetardsAnnee++;
        if (this.nombreRetardsAnnee >= 3) {
            this.suspendu = true;
        }
    }

    public void setSuspendu(boolean suspendu) { this.suspendu = suspendu; }
    public void setNombreRetardsAnnee(int n) { this.nombreRetardsAnnee = n; }
}
