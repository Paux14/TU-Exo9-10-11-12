package rooms.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rooms.exception.AlreadyCancelledException;
import rooms.exception.ReservationNotFoundException;
import rooms.exception.RoomNotFoundException;
import rooms.exception.TimeSlotConflictException;
import rooms.model.Reservation;
import rooms.model.ReservationStatus;
import rooms.service.ReservationService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ReservationService reservationService;

    private static final String VALID_BODY =
            "{\"roomId\":\"room-1\",\"bookerName\":\"Alice\",\"start\":\"2025-01-15T09:00:00\",\"end\":\"2025-01-15T10:00:00\"}";

    @Test
    void createReservation_valid_shouldReturn201() throws Exception {
        Reservation reservation = new Reservation(
                "res-1", "room-1", "Alice",
                LocalDateTime.of(2025, 1, 15, 9, 0),
                LocalDateTime.of(2025, 1, 15, 10, 0));
        when(reservationService.create(any(), any(), any(), any())).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void createReservation_roomNotFound_shouldReturn404() throws Exception {
        when(reservationService.create(any(), any(), any(), any()))
                .thenThrow(new RoomNotFoundException("room-1"));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReservation_conflict_shouldReturn409() throws Exception {
        when(reservationService.create(any(), any(), any(), any()))
                .thenThrow(new TimeSlotConflictException());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void createReservation_missingBookerName_shouldReturn400() throws Exception {
        String body = "{\"roomId\":\"room-1\",\"start\":\"2025-01-15T09:00:00\",\"end\":\"2025-01-15T10:00:00\"}";

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReservation_existing_shouldReturn200() throws Exception {
        Reservation reservation = new Reservation(
                "res-1", "room-1", "Alice",
                LocalDateTime.of(2025, 1, 15, 9, 0),
                LocalDateTime.of(2025, 1, 15, 10, 0));
        when(reservationService.findById("res-1")).thenReturn(reservation);

        mockMvc.perform(get("/api/reservations/res-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-1"));
    }

    @Test
    void getReservation_notFound_shouldReturn404() throws Exception {
        when(reservationService.findById("unknown")).thenThrow(new ReservationNotFoundException("unknown"));

        mockMvc.perform(get("/api/reservations/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelReservation_confirmed_shouldReturn200() throws Exception {
        Reservation cancelled = new Reservation(
                "res-1", "room-1", "Alice",
                LocalDateTime.of(2025, 1, 15, 9, 0),
                LocalDateTime.of(2025, 1, 15, 10, 0));
        cancelled.setStatus(ReservationStatus.CANCELLED);
        when(reservationService.cancel("res-1")).thenReturn(cancelled);

        mockMvc.perform(patch("/api/reservations/res-1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelReservation_alreadyCancelled_shouldReturn409() throws Exception {
        when(reservationService.cancel("res-1")).thenThrow(new AlreadyCancelledException("res-1"));

        mockMvc.perform(patch("/api/reservations/res-1/cancel"))
                .andExpect(status().isConflict());
    }
}
