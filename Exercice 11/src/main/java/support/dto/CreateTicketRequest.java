package support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import support.model.Priority;

public class CreateTicketRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, message = "Le titre doit contenir au moins 3 caractères")
    private String title;

    @NotNull(message = "La priorité est obligatoire")
    private Priority priority;

    public CreateTicketRequest() {}

    public CreateTicketRequest(String title, Priority priority) {
        this.title = title;
        this.priority = priority;
    }

    public String getTitle()      { return title; }
    public Priority getPriority() { return priority; }
    public void setTitle(String title)         { this.title = title; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
