package mediacity.repository;

import mediacity.model.Pret;
import mediacity.model.StatutPret;

import java.util.*;
import java.util.stream.Collectors;

public class PretRepository {

    private final Map<String, Pret> store = new HashMap<>();

    public void save(Pret pret) {
        store.put(pret.getId(), pret);
    }

    public Optional<Pret> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Pret> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Pret> findByOuvrageId(String ouvrageId) {
        return store.values().stream()
                .filter(p -> p.getOuvrage().getId().equals(ouvrageId))
                .collect(Collectors.toList());
    }

    public boolean existsPretEnCoursPourOuvrage(String ouvrageId) {
        return store.values().stream()
                .anyMatch(p -> p.getOuvrage().getId().equals(ouvrageId)
                        && p.getStatut() == StatutPret.EN_COURS);
    }
}
