package rooms.repository;

import org.springframework.stereotype.Repository;
import rooms.model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ReservationRepository {
    private final Map<String, Reservation> store = new ConcurrentHashMap<>();

    public Reservation save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
        return reservation;
    }

    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Reservation> findByRoomId(String roomId) {
        return store.values().stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }

    public void clear() {
        store.clear();
    }
}
