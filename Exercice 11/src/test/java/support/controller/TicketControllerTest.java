package support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import support.dto.CreateTicketRequest;
import support.dto.UpdateStatusRequest;
import support.exception.InvalidStatusTransitionException;
import support.exception.TicketNotFoundException;
import support.model.Priority;
import support.model.Status;
import support.model.Ticket;
import support.service.TicketService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService service;

    @Test
    void createTicket_shouldReturn201WithTicket() throws Exception {
        Ticket ticket = new Ticket("id-1", "Problème réseau", Priority.HIGH);
        when(service.create("Problème réseau", Priority.HIGH)).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTicketRequest("Problème réseau", Priority.HIGH))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.title").value("Problème réseau"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createTicket_withBlankTitle_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"priority\":\"HIGH\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createTicket_withNullPriority_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titre valide\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getTicket_shouldReturn200WithTicket() throws Exception {
        Ticket ticket = new Ticket("id-1", "Bug critique", Priority.MEDIUM);
        when(service.findById("id-1")).thenReturn(ticket);

        mockMvc.perform(get("/api/tickets/id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void getTicket_whenNotFound_shouldReturn404() throws Exception {
        when(service.findById("unknown")).thenThrow(new TicketNotFoundException("unknown"));

        mockMvc.perform(get("/api/tickets/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAllTickets_shouldReturn200WithList() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new Ticket("id-1", "Ticket 1", Priority.LOW),
                new Ticket("id-2", "Ticket 2", Priority.HIGH)
        ));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateStatus_shouldReturn200WithUpdatedTicket() throws Exception {
        Ticket ticket = new Ticket("id-1", "Test", Priority.HIGH);
        ticket.setStatus(Status.IN_PROGRESS);
        when(service.updateStatus(eq("id-1"), eq(Status.IN_PROGRESS))).thenReturn(ticket);

        mockMvc.perform(patch("/api/tickets/id-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateStatusRequest(Status.IN_PROGRESS))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateStatus_whenNotFound_shouldReturn404() throws Exception {
        when(service.updateStatus(eq("unknown"), any()))
                .thenThrow(new TicketNotFoundException("unknown"));

        mockMvc.perform(patch("/api/tickets/unknown/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateStatusRequest(Status.IN_PROGRESS))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_whenInvalidTransition_shouldReturn409() throws Exception {
        when(service.updateStatus(eq("id-1"), eq(Status.IN_PROGRESS)))
                .thenThrow(new InvalidStatusTransitionException(Status.RESOLVED, Status.IN_PROGRESS));

        mockMvc.perform(patch("/api/tickets/id-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateStatusRequest(Status.IN_PROGRESS))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
}
