package com.smartlibrary.service;

import com.smartlibrary.model.Reservation;
import com.smartlibrary.model.Seat;
import com.smartlibrary.strategy.ReservationStrategy;
import com.smartlibrary.waitlist.Waitlist;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReservationService {
    private NotificationService notificationService;
    private List<Seat> seats;
    private List<Reservation> reservations;
    private Waitlist waitlist;
    private int reservationCounter;

    public ReservationService() {
        this(null);
    }

    public ReservationService(NotificationService notificationService) {
        this.seats = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.waitlist = new Waitlist();
        this.reservationCounter = 1;
        this.notificationService = notificationService;

        seats.add(new Seat(1));
        seats.add(new Seat(2));
        seats.add(new Seat(3));
    }

    public void reserveSeat(String userId, ReservationStrategy strategy, Scanner scanner) {
        checkExpiredReservationsSilently();

        for (Reservation reservation : reservations) {
            if (reservation.getUserId().equals(userId) && reservation.isActive()) {
                System.out.println("You already have an active seat reservation. Please cancel it before making a new one.");
                return;
            }
        }

        List<Seat> availableSeats = new ArrayList<>();

        System.out.println("Available seats:");
        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                availableSeats.add(seat);
                System.out.println(seat);
            }
        }

        if (availableSeats.isEmpty()) {
            System.out.print("No available seats. Do you want to join the waitlist? (yes/no), or press M to return to the main menu: ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("m")) {
                System.out.println("Returning to main menu...");
                return;
            }

            if (answer.equalsIgnoreCase("yes")) {
                waitlist.addStudent(userId);
            } else {
                System.out.println("You were not added to the waitlist.");
            }
            return;
        }

        System.out.print("Enter the seat ID you want to reserve, or press M to return to the main menu: ");
        String seatInput = scanner.nextLine().trim();

        if (seatInput.equalsIgnoreCase("m")) {
            System.out.println("Returning to main menu...");
            return;
        }

        int selectedSeatId;

        try {
            selectedSeatId = Integer.parseInt(seatInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu...");
            return;
        }

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
                userId,
                selectedSeat,
                strategy.getExpirationMinutes()
        );

        reservations.add(reservation);

        System.out.println("Reservation successful. Details sent as notification:");
        notificationService.notifySeatReserved(userId, selectedSeatId);
    }

    public void cancelReservation(String userId, Scanner scanner) {
        checkExpiredReservationsSilently();

        Reservation activeReservation = null;

        for (Reservation reservation : reservations) {
            if (reservation.getUserId().equals(userId) && reservation.isActive()) {
                activeReservation = reservation;
                break;
            }
        }

        if (activeReservation == null) {
            System.out.println("You do not have an active reservation to cancel.");
            return;
        }

        System.out.println("You have an active reservation:");
        System.out.println(activeReservation);
        System.out.print("Do you want to cancel this reservation? (yes/no), or press M to return to the main menu: ");
        String answer = scanner.nextLine().trim();

        if (answer.equalsIgnoreCase("m")) {
            System.out.println("Returning to main menu...");
            return;
        }

        if (answer.equalsIgnoreCase("yes")) {
            activeReservation.cancelReservation();

            System.out.println("Reservation cancelled. Details sent as notification:");

            if (notificationService != null) {
                notificationService.notifyReservationCancelled(userId);
            } else {
                System.out.println("Your reservation has been cancelled.");
            }

            assignSeatToNextWaitlistedStudent();
        } else {
            System.out.println("Reservation was not cancelled.");
        }
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
            String nextUser = waitlist.getNextStudent();

            if (notificationService != null) {
                notificationService.notifySeatAvailable(nextUser);
            } else {
                System.out.println("A seat is now available for waitlisted user: " + nextUser);
            }
        }
    }

    public void showMyReservations(String userId) {
        checkExpiredReservationsSilently();

        boolean found = false;

        for (Reservation reservation : reservations) {
            if (reservation.getUserId().equals(userId)) {
                System.out.println(reservation);
                found = true;
            }
        }

        if (!found) {
            System.out.println("You do not have any reservations yet.");
        }
    }

    public void showMyReservations(String userId, Scanner scanner) {
        showMyReservations(userId);
    }

    public void showWaitlistStatus(String userId) {
        if (!waitlist.containsStudent(userId)) {
            System.out.println("You are not in the waitlist.");
            return;
        }

        int position = waitlist.getStudentPosition(userId);

        System.out.println("You are currently in the waitlist.");
        System.out.println("Your waitlist position: " + position);
    }

    public void showWaitlist(String userId) {
        showWaitlistStatus(userId);
    }

    public void showAllSeats() {
        for (Seat seat : seats) {
            System.out.println(seat);
        }
    }

    public void showAllReservations() {
        for (Reservation reservation : reservations) {
            System.out.println(reservation);
        }
    }

    public void runObserverSimulation() {
        System.out.println("\n=== Observer Pattern Simulation ===");

        String userA = "User A";
        String userB = "User B";

        Seat demoSeat = new Seat(1);
        Waitlist demoWaitlist = new Waitlist();

        System.out.println("\nStep 1: " + userA + " reserves Seat 1.");
        demoSeat.reserveSeat();
        System.out.println("Seat 1 is now busy.");

        System.out.println("\nStep 2: " + userB + " tries to reserve a seat.");
        System.out.println("No available seats.");
        demoWaitlist.addStudentSilently(userB);
        System.out.println(userB + " has been added to the waitlist.");
        System.out.println("User B's waitlist position: " + demoWaitlist.getStudentPosition(userB) + ".");

        System.out.println("\nStep 3: " + userA + " cancels the reservation.");
        demoSeat.releaseSeat();
        System.out.println("Seat 1 is released and becomes available again.");

        System.out.println("\nStep 4: Observer notification is triggered.");
        String nextUser = demoWaitlist.getNextStudent();

        if (notificationService != null) {
            notificationService.notifySeatAvailable(nextUser, demoSeat.getSeatId());
        }

        System.out.println("Notification sent to " + nextUser + ": Seat " + demoSeat.getSeatId() + " is now available. Do you want to reserve it?");
    }

}

