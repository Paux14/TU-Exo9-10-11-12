package support.repository;

import org.springframework.stereotype.Repository;
import support.model.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TicketRepository {
    private final Map<String, Ticket> store = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket) {
        store.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Ticket> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clear() {
        store.clear();
    }
}
