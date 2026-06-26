package mediacity.repository;

import mediacity.model.Ouvrage;

import java.util.*;

public class OuvrageRepository {

    private final Map<String, Ouvrage> store = new HashMap<>();

    public void save(Ouvrage ouvrage) {
        store.put(ouvrage.getId(), ouvrage);
    }

    public Optional<Ouvrage> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Ouvrage> findAll() {
        return new ArrayList<>(store.values());
    }
}
