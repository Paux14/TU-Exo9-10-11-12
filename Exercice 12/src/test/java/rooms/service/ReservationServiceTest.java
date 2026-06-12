package rooms.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RoomRepository roomRepository;
    @InjectMocks private ReservationService service;

    private final String roomId = "room-1";
    private final LocalDateTime start = LocalDateTime.of(2025, 1, 15, 9, 0);
    private final LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 0);

    @Test
    void createReservation_valid_shouldReturnConfirmed() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(reservationRepository.findByRoomId(roomId)).thenReturn(List.of());
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = service.create(roomId, "Alice", start, end);

        assertEquals(ReservationStatus.CONFIRMED, result.getStatus());
        assertEquals(roomId, result.getRoomId());
        assertNotNull(result.getId());
    }

    @Test
    void createReservation_roomNotFound_shouldThrow() {
        when(roomRepository.existsById(roomId)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> service.create(roomId, "Alice", start, end));
    }

    @Test
    void createReservation_endBeforeStart_shouldThrow() {
        when(roomRepository.existsById(roomId)).thenReturn(true);

        assertThrows(InvalidTimeSlotException.class, () -> service.create(roomId, "Alice", end, start));
    }

    @Test
    void createReservation_equalStartEnd_shouldThrow() {
        when(roomRepository.existsById(roomId)).thenReturn(true);

        assertThrows(InvalidTimeSlotException.class, () -> service.create(roomId, "Alice", start, start));
    }

    @Test
    void createReservation_overlappingConfirmed_shouldThrow() {
        Reservation existing = new Reservation("existing", roomId, "Bob", start, end);
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(reservationRepository.findByRoomId(roomId)).thenReturn(List.of(existing));

        assertThrows(TimeSlotConflictException.class,
                () -> service.create(roomId, "Charlie", start.plusMinutes(30), end.plusMinutes(30)));
    }

    @Test
    void createReservation_cancelledSlot_shouldSucceed() {
        Reservation cancelled = new Reservation("cancelled", roomId, "Bob", start, end);
        cancelled.setStatus(ReservationStatus.CANCELLED);
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(reservationRepository.findByRoomId(roomId)).thenReturn(List.of(cancelled));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = service.create(roomId, "Alice", start, end);

        assertEquals(ReservationStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void cancelReservation_confirmed_shouldSetCancelled() {
        Reservation reservation = new Reservation("res-1", roomId, "Alice", start, end);
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = service.cancel("res-1");

        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelReservation_alreadyCancelled_shouldThrow() {
        Reservation reservation = new Reservation("res-1", roomId, "Alice", start, end);
        reservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));

        assertThrows(AlreadyCancelledException.class, () -> service.cancel("res-1"));
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(reservationRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> service.findById("unknown"));
    }
}
