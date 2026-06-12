package support.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import support.dto.CreateTicketRequest;
import support.dto.UpdateStatusRequest;
import support.model.Priority;
import support.model.Status;
import support.repository.TicketRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.clear();
    }

    @Test
    void fullJourney_createGetThenUpdateStatus() throws Exception {
        CreateTicketRequest createReq = new CreateTicketRequest("Problème réseau", Priority.HIGH);
        String createResponse = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andReturn().getResponse().getContentAsString();

        String ticketId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(get("/api/tickets/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.title").value("Problème réseau"));

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateStatusRequest(Status.RESOLVED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    void createTicket_withInvalidTitle_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"ab\",\"priority\":\"LOW\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getTicket_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/tickets/id-inexistant"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateStatus_resolvedTicket_shouldReturn409() throws Exception {
        String createResponse = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTicketRequest("Ticket test", Priority.MEDIUM))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ticketId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateStatusRequest(Status.RESOLVED))))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateStatusRequest(Status.IN_PROGRESS))))
                .andExpect(status().isConflict());
    }
}
