package mediacity.repository;

import mediacity.model.Adherent;

import java.util.*;

public class AdherentRepository {

    private final Map<String, Adherent> store = new HashMap<>();

    public void save(Adherent adherent) {
        store.put(adherent.getId(), adherent);
    }

    public Optional<Adherent> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
