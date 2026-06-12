package rooms.service;

import org.springframework.stereotype.Service;
import rooms.exception.AlreadyCancelledException;
import rooms.exception.InvalidTimeSlotException;
import rooms.exception.ReservationNotFoundException;
import rooms.exception.RoomNotFoundException;
import rooms.exception.TimeSlotConflictException;
import rooms.model.Reservation;
import rooms.model.ReservationStatus;
import rooms.repository.ReservationRepository;
import rooms.repository.RoomRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation create(String roomId, String bookerName, LocalDateTime start, LocalDateTime end) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        if (!end.isAfter(start)) {
            throw new InvalidTimeSlotException();
        }
        boolean conflict = reservationRepository.findByRoomId(roomId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .anyMatch(r -> start.isBefore(r.getEnd()) && end.isAfter(r.getStart()));
        if (conflict) {
            throw new TimeSlotConflictException();
        }
        Reservation reservation = new Reservation(UUID.randomUUID().toString(), roomId, bookerName, start, end);
        return reservationRepository.save(reservation);
    }

    public Reservation findById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public Reservation cancel(String id) {
        Reservation reservation = findById(id);
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new AlreadyCancelledException(id);
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }
}
