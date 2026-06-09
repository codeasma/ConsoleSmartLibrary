package com.smartlibrary.model;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private String userId;
    private Seat seat;
    private LocalDateTime reservationTime;
    private LocalDateTime arrivalExpirationTime;
    private LocalDateTime reservationEndTime;
    private boolean active;

    public Reservation(int reservationId, String userId, Seat seat, int reservationDurationHours, int expirationMinutes) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.seat = seat;
        this.reservationTime = LocalDateTime.now();
        this.arrivalExpirationTime = reservationTime.plusMinutes(expirationMinutes);
        this.reservationEndTime = reservationTime.plusHours(reservationDurationHours);
        this.active = true;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getArrivalExpirationTime() {
        return arrivalExpirationTime;
    }

    public LocalDateTime getReservationEndTime() {
        return reservationEndTime;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(arrivalExpirationTime);
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
                " | Arrival Expiration: " + arrivalExpirationTime.format(formatter) +
                " | Reservation Ends: " + reservationEndTime.format(formatter) +
                " | Status: " + status;
    }
}