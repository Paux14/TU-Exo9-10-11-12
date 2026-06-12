package support.service;

import org.springframework.stereotype.Service;
import support.exception.InvalidStatusTransitionException;
import support.exception.TicketNotFoundException;
import support.model.Priority;
import support.model.Status;
import support.model.Ticket;
import support.repository.TicketRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public Ticket create(String title, Priority priority) {
        Ticket ticket = new Ticket(UUID.randomUUID().toString(), title, priority);
        return repository.save(ticket);
    }

    public Ticket findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public List<Ticket> findAll() {
        return repository.findAll();
    }

    public Ticket updateStatus(String id, Status newStatus) {
        Ticket ticket = findById(id);
        if (!isTransitionAllowed(ticket.getStatus(), newStatus)) {
            throw new InvalidStatusTransitionException(ticket.getStatus(), newStatus);
        }
        ticket.setStatus(newStatus);
        return repository.save(ticket);
    }

    private boolean isTransitionAllowed(Status current, Status next) {
        return switch (current) {
            case OPEN -> next == Status.IN_PROGRESS || next == Status.RESOLVED;
            case IN_PROGRESS -> next == Status.RESOLVED;
            case RESOLVED -> false;
        };
    }
}
