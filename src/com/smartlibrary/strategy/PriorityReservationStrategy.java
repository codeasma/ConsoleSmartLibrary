package com.smartlibrary.strategy;

public class PriorityReservationStrategy implements ReservationStrategy {

    @Override
    public int getReservationDuration() {
        return 4; // 4 hours
    }

    @Override
    public int getExpirationMinutes() {
        return 30; // priority reservation expires after 30 minutes if the student does not arrive
    }
}