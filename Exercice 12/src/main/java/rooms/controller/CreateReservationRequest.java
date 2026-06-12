package rooms.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateReservationRequest {
    @NotBlank
    private String roomId;
    @NotBlank
    private String bookerName;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getBookerName() { return bookerName; }
    public void setBookerName(String bookerName) { this.bookerName = bookerName; }
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }
}
