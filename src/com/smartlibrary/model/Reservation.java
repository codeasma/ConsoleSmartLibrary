package com.smartlibrary.model;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private String studentId;
    private Seat seat;
    private LocalDateTime reservationTime;
    private LocalDateTime expirationTime;
    private boolean active;

    public Reservation(int reservationId, String studentId, Seat seat, int expirationMinutes) {
        this.reservationId = reservationId;
        this.studentId = studentId;
        this.seat = seat;
        this.reservationTime = LocalDateTime.now();
        this.expirationTime = reservationTime.plusMinutes(expirationMinutes);
        this.active = true;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    public void cancelReservation() {
        this.active = false;
        seat.releaseSeat();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String status = active ? "Active" : "Cancelled / Expired";

        return "Seat ID: " + seat.getSeatId() +
                " | Reservation Time: " + reservationTime.format(formatter) +
                " | Expiration Time: " + expirationTime.format(formatter) +
                " | Status: " + status;
    }
}