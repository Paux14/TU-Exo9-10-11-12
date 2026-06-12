package rooms.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rooms.model.Room;
import rooms.service.RoomService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private RoomService roomService;

    @Test
    void createRoom_valid_shouldReturn201() throws Exception {
        when(roomService.create("Salle A", 10)).thenReturn(new Room("room-1", "Salle A", 10));

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Salle A\",\"capacity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Salle A"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    void createRoom_missingName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"capacity\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_capacityZero_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Salle A\",\"capacity\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_blankName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"capacity\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listRooms_shouldReturn200WithRooms() throws Exception {
        when(roomService.findAll()).thenReturn(List.of(new Room("1", "Salle A", 5)));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Salle A"));
    }
}
