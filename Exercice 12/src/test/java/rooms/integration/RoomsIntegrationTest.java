package rooms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rooms.model.Reservation;
import rooms.model.Room;
import rooms.repository.ReservationRepository;
import rooms.repository.RoomRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RoomsIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoomRepository roomRepository;
    @Autowired private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.clear();
        roomRepository.clear();
    }

    @Test
    void fullFlow_createRoomReserveConsultCancel() throws Exception {
        String roomResponse = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Salle Conference\",\"capacity\":10}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Room room = objectMapper.readValue(roomResponse, Room.class);

        String reservationBody = String.format(
                "{\"roomId\":\"%s\",\"bookerName\":\"Alice\",\"start\":\"2025-06-15T10:00:00\",\"end\":\"2025-06-15T11:00:00\"}",
                room.getId());

        String resResponse = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn().getResponse().getContentAsString();

        Reservation reservation = objectMapper.readValue(resResponse, Reservation.class);

        mockMvc.perform(get("/api/reservations/" + reservation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookerName").value("Alice"));

        mockMvc.perform(patch("/api/reservations/" + reservation.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void createReservation_unknownRoom_shouldReturn404() throws Exception {
        String body = "{\"roomId\":\"unknown\",\"bookerName\":\"Bob\",\"start\":\"2025-06-15T10:00:00\",\"end\":\"2025-06-15T11:00:00\"}";

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
