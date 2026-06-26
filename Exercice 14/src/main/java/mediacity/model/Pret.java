package mediacity.model;

import java.time.LocalDate;

public class Pret {

    private static final int DUREE_PRET_JOURS = 21;
    private static final double PENALITE_PAR_JOUR = 0.15;

    private final String id;
    private final Ouvrage ouvrage;
    private final Adherent adherent;
    private final LocalDate dateDebut;
    private final LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private StatutPret statut;

    public Pret(String id, Ouvrage ouvrage, Adherent adherent, LocalDate dateDebut) {
        this.id = id;
        this.ouvrage = ouvrage;
        this.adherent = adherent;
        this.dateDebut = dateDebut;
        this.dateRetourPrevue = dateDebut.plusDays(DUREE_PRET_JOURS);
        this.statut = StatutPret.EN_COURS;
    }

    public double calculerPenalite(LocalDate dateRetour) {
        if (!dateRetour.isAfter(dateRetourPrevue)) {
            return 0.0;
        }
        long joursRetard = dateRetourPrevue.until(dateRetour).getDays();
        return joursRetard * PENALITE_PAR_JOUR;
    }

    public boolean estEnRetard(LocalDate dateRetour) {
        return dateRetour.isAfter(dateRetourPrevue);
    }

    public String getId() { return id; }
    public Ouvrage getOuvrage() { return ouvrage; }
    public Adherent getAdherent() { return adherent; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public StatutPret getStatut() { return statut; }

    public void setDateRetourEffective(LocalDate date) { this.dateRetourEffective = date; }
    public void setStatut(StatutPret statut) { this.statut = statut; }
}
