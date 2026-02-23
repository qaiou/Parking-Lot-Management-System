package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketId;
    private String plateNumber;
    private String spotId;
    private LocalDateTime entryTime;

    public Ticket(String plateNumber, String spotId) {
        this.plateNumber = plateNumber;
        this.spotId = spotId;
        this.entryTime = LocalDateTime.now();
        this.ticketId = generateTicketId();
    }

    private String generateTicketId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return "T-" + plateNumber + "-" + entryTime.format(formatter);
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
        // Regenerate ticket ID with the correct entry time
        this.ticketId = generateTicketId();
    }

    @Override
    public String toString() {
        return ticketId;
    }
}