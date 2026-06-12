package rooms.model;

import java.time.LocalDateTime;

public class Reservation {
    private String id;
    private String roomId;
    private String bookerName;
    private LocalDateTime start;
    private LocalDateTime end;
    private ReservationStatus status;

    public Reservation() {}

    public Reservation(String id, String roomId, String bookerName, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.roomId = roomId;
        this.bookerName = bookerName;
        this.start = start;
        this.end = end;
        this.status = ReservationStatus.CONFIRMED;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getBookerName() { return bookerName; }
    public void setBookerName(String bookerName) { this.bookerName = bookerName; }
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
