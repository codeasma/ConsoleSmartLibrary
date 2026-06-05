package com.smartlibrary.model;

public class Seat {
    private int seatId;
    private boolean available;

    public Seat(int seatId) {
        this.seatId = seatId;
        this.available = true;
    }

    public int getSeatId() {
        return seatId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void reserveSeat() {
        this.available = false;
    }

    public void releaseSeat() {
        this.available = true;
    }

    @Override
    public String toString() {
        String status = available ? "Available" : "Busy";
        return "Seat " + seatId + " - " + status;
    }
}