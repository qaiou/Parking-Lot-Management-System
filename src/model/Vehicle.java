package model;

import java.time.LocalDateTime;

public abstract class Vehicle {
    protected String plateNumber;
    protected LocalDateTime entryTime;
    protected Ticket ticket;

    public Vehicle(String plateNumber) {
        this.plateNumber = plateNumber;
        this.entryTime = LocalDateTime.now();
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public abstract String getType();
}