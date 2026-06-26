package mediacity.service;

import mediacity.exception.AdherentSuspenduException;
import mediacity.exception.OuvrageIndisponibleException;
import mediacity.exception.PretIntrouvableException;
import mediacity.model.*;
import mediacity.repository.PretRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PretService {

    private final PretRepository pretRepository;

    public PretService(PretRepository pretRepository) {
        this.pretRepository = pretRepository;
    }

    public Pret creerPret(Ouvrage ouvrage, Adherent adherent, LocalDate dateDebut) {
        if (adherent.isSuspendu()) {
            throw new AdherentSuspenduException(adherent.getNom());
        }
        if (!ouvrage.isDisponible()) {
            throw new OuvrageIndisponibleException(ouvrage.getTitre());
        }
        Pret pret = new Pret(UUID.randomUUID().toString(), ouvrage, adherent, dateDebut);
        ouvrage.setDisponible(false);
        pretRepository.save(pret);
        return pret;
    }

    public double retournerOuvrage(String pretId, LocalDate dateRetour) {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new PretIntrouvableException(pretId));

        double penalite = pret.calculerPenalite(dateRetour);

        if (pret.estEnRetard(dateRetour)) {
            pret.setStatut(StatutPret.EN_RETARD);
            pret.getAdherent().incrementerRetards();
        } else {
            pret.setStatut(StatutPret.RENDU);
        }

        pret.setDateRetourEffective(dateRetour);
        pret.getOuvrage().setDisponible(true);
        return penalite;
    }

    public List<Pret> listerPrets() {
        return pretRepository.findAll();
    }
}
