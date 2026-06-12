package rooms.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rooms.model.Reservation;
import rooms.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation create(@Valid @RequestBody CreateReservationRequest request) {
        return service.create(request.getRoomId(), request.getBookerName(), request.getStart(), request.getEnd());
    }

    @GetMapping("/{id}")
    public Reservation findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable String id) {
        return service.cancel(id);
    }
}
