package rooms.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rooms.model.Room;
import rooms.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room create(@Valid @RequestBody CreateRoomRequest request) {
        return service.create(request.getName(), request.getCapacity());
    }

    @GetMapping
    public List<Room> findAll() {
        return service.findAll();
    }
}
