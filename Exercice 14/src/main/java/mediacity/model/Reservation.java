package mediacity.model;

import java.time.LocalDate;

public class Reservation {

    private final String id;
    private final Ouvrage ouvrage;
    private final Adherent adherent;
    private final LocalDate dateReservation;
    private StatutReservation statut;

    public Reservation(String id, Ouvrage ouvrage, Adherent adherent, LocalDate dateReservation) {
        this.id = id;
        this.ouvrage = ouvrage;
        this.adherent = adherent;
        this.dateReservation = dateReservation;
        this.statut = StatutReservation.EN_ATTENTE;
    }

    public String getId() { return id; }
    public Ouvrage getOuvrage() { return ouvrage; }
    public Adherent getAdherent() { return adherent; }
    public LocalDate getDateReservation() { return dateReservation; }
    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }
}
