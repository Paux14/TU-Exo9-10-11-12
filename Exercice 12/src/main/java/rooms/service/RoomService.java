package rooms.service;

import org.springframework.stereotype.Service;
import rooms.model.Room;
import rooms.repository.RoomRepository;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public Room create(String name, int capacity) {
        Room room = new Room(UUID.randomUUID().toString(), name, capacity);
        return repository.save(room);
    }

    public List<Room> findAll() {
        return repository.findAll();
    }
}
