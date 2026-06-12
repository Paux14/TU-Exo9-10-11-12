package support.model;

public class Ticket {
    private final String id;
    private final String title;
    private final Priority priority;
    private Status status;

    public Ticket(String id, String title, Priority priority) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = Status.OPEN;
    }

    public String getId()       { return id; }
    public String getTitle()    { return title; }
    public Priority getPriority(){ return priority; }
    public Status getStatus()   { return status; }

    public void setStatus(Status status) { this.status = status; }
}
