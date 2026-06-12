package support.dto;

import jakarta.validation.constraints.NotNull;
import support.model.Status;

public class UpdateStatusRequest {

    @NotNull(message = "Le statut est obligatoire")
    private Status status;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(Status status) {
        this.status = status;
    }

    public Status getStatus()             { return status; }
    public void setStatus(Status status)  { this.status = status; }
}
