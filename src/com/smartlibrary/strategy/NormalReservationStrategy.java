package com.smartlibrary.strategy;

public class NormalReservationStrategy implements ReservationStrategy {

    @Override
    public int getReservationDuration() {
        return 2; // 2 hours
    }

    @Override
    public int getExpirationMinutes() {
        return 15; // reservation expires after 15 minutes if the user does not arrive
    }
}

