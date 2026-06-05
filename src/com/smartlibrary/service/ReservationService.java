package com.smartlibrary.service;

import com.smartlibrary.model.Reservation;
import com.smartlibrary.model.Seat;
import com.smartlibrary.strategy.ReservationStrategy;
import com.smartlibrary.waitlist.Waitlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReservationService {
    private List<Seat> seats;
    private List<Reservation> reservations;
    private Waitlist waitlist;
    private int reservationCounter;

    public ReservationService() {
        this.seats = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.waitlist = new Waitlist();
        this.reservationCounter = 1;

        // Example seats
        seats.add(new Seat(1));
        seats.add(new Seat(2));
        seats.add(new Seat(3));
    }

    public void reserveSeat(String studentId, ReservationStrategy strategy, Scanner scanner) {
        checkExpiredReservationsSilently();

        List<Seat> availableSeats = new ArrayList<>();

        System.out.println("Available seats:");
        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                availableSeats.add(seat);
                System.out.println(seat);
            }
        }

        if (availableSeats.isEmpty()) {
            System.out.print("No available seats. Do you want to join the waitlist? (yes/no): ");
            String answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("yes")) {
                waitlist.addStudent(studentId);
            } else {
                System.out.println("You were not added to the waitlist.");
            }
            return;
        }

        System.out.print("Enter the seat ID you want to reserve: ");
        int selectedSeatId = scanner.nextInt();
        scanner.nextLine();

        Seat selectedSeat = null;
        for (Seat seat : availableSeats) {
            if (seat.getSeatId() == selectedSeatId) {
                selectedSeat = seat;
                break;
            }
        }

        if (selectedSeat == null) {
            System.out.println("Invalid seat selection.");
            return;
        }

        selectedSeat.reserveSeat();

        Reservation reservation = new Reservation(
                reservationCounter++,
                studentId,
                selectedSeat,
                strategy.getExpirationMinutes()
        );

        reservations.add(reservation);

        System.out.println("Seat reserved successfully.");
        System.out.println("Reservation duration: " + strategy.getReservationDuration() + " hours");
        System.out.println("Arrival expiration time: " + strategy.getExpirationMinutes() + " minutes");
        System.out.println(reservation);
    }

    public void cancelReservation(int reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId() == reservationId && reservation.isActive()) {
                reservation.cancelReservation();
                System.out.println("Reservation cancelled successfully.");

                assignSeatToNextWaitlistedStudent();
                return;
            }
        }

        System.out.println("Reservation not found or already inactive.");
    }

    private void checkExpiredReservationsSilently() {
        for (Reservation reservation : reservations) {
            if (reservation.isActive() && reservation.isExpired()) {
                reservation.cancelReservation();
                assignSeatToNextWaitlistedStudent();
            }
        }
    }

    private void assignSeatToNextWaitlistedStudent() {
        if (!waitlist.isEmpty()) {
            String nextStudent = waitlist.getNextStudent();
            System.out.println("Seat is now available for waitlisted student: " + nextStudent);
        }
    }

    public void showMyReservations(String studentId, Scanner scanner) {
        checkExpiredReservationsSilently();

        List<Reservation> myReservations = new ArrayList<>();
        List<Reservation> activeReservations = new ArrayList<>();

        for (Reservation reservation : reservations) {
            if (reservation.getStudentId().equals(studentId)) {
                myReservations.add(reservation);
                System.out.println(reservation);

                if (reservation.isActive()) {
                    activeReservations.add(reservation);
                }
            }
        }

        if (myReservations.isEmpty()) {
            System.out.println("You do not have any reservations yet.");
            return;
        }

        if (activeReservations.isEmpty()) {
            System.out.println("You do not have any active reservations to cancel.");
            return;
        }

        System.out.print("Do you want to cancel an active reservation? (yes/no): ");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("yes")) {
            System.out.print("Enter reservation ID to cancel: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            cancelReservation(reservationId);
        }
    }

    public void showWaitlist() {
        System.out.println(waitlist);
    }
}