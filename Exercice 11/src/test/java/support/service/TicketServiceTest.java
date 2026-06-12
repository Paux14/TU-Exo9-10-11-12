package support.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.exception.InvalidStatusTransitionException;
import support.exception.TicketNotFoundException;
import support.model.Priority;
import support.model.Status;
import support.model.Ticket;
import support.repository.TicketRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository repository;

    @InjectMocks
    private TicketService service;

    @Test
    void createTicket_shouldSetStatusToOpen() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.create("Problème réseau", Priority.HIGH);

        assertEquals(Status.OPEN, result.getStatus());
    }

    @Test
    void createTicket_shouldPersistWithGeneratedId() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.create("Bug critique", Priority.MEDIUM);

        assertNotNull(result.getId());
        assertEquals("Bug critique", result.getTitle());
        assertEquals(Priority.MEDIUM, result.getPriority());
    }

    @Test
    void findById_shouldReturnExistingTicket() {
        Ticket ticket = new Ticket("id-1", "Test", Priority.LOW);
        when(repository.findById("id-1")).thenReturn(Optional.of(ticket));

        Ticket result = service.findById("id-1");

        assertEquals("id-1", result.getId());
        assertEquals("Test", result.getTitle());
    }

    @Test
    void findById_shouldThrowWhenTicketNotFound() {
        when(repository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> service.findById("unknown"));
    }

    @Test
    void updateStatus_openToInProgress_shouldSucceed() {
        Ticket ticket = new Ticket("id-1", "Test", Priority.HIGH);
        when(repository.findById("id-1")).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.updateStatus("id-1", Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void updateStatus_openToResolved_shouldSucceed() {
        Ticket ticket = new Ticket("id-1", "Test", Priority.HIGH);
        when(repository.findById("id-1")).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.updateStatus("id-1", Status.RESOLVED);

        assertEquals(Status.RESOLVED, result.getStatus());
    }

    @Test
    void updateStatus_inProgressToResolved_shouldSucceed() {
        Ticket ticket = new Ticket("id-1", "Test", Priority.MEDIUM);
        ticket.setStatus(Status.IN_PROGRESS);
        when(repository.findById("id-1")).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.updateStatus("id-1", Status.RESOLVED);

        assertEquals(Status.RESOLVED, result.getStatus());
    }

    @Test
    void updateStatus_resolvedTicket_shouldThrowConflict() {
        Ticket ticket = new Ticket("id-1", "Test", Priority.HIGH);
        ticket.setStatus(Status.RESOLVED);
        when(repository.findById("id-1")).thenReturn(Optional.of(ticket));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateStatus("id-1", Status.IN_PROGRESS));
    }

    @Test
    void updateStatus_inProgressToOpen_shouldThrowConflict() {
        Ticket ticket = new Ticket("id-1", "Test", Priority.LOW);
        ticket.setStatus(Status.IN_PROGRESS);
        when(repository.findById("id-1")).thenReturn(Optional.of(ticket));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateStatus("id-1", Status.OPEN));
    }
}
