package com.smartlibrary;

import com.smartlibrary.service.ReservationService;
import com.smartlibrary.strategy.NormalReservationStrategy;
import com.smartlibrary.strategy.PriorityReservationStrategy;
import com.smartlibrary.strategy.ReservationStrategy;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ReservationService reservationService = new ReservationService(null);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Smart Library System");
        System.out.print("Please enter your user ID to log in: ");
        String loggedInUserId = scanner.nextLine();

        while (true) {
            System.out.println("\nSmart Library System Menu");
            System.out.println("1. Reserve Seat as Normal User");
            System.out.println("2. Reserve Seat as Priority User");
            System.out.println("3. My Reservations");
            System.out.println("4. Cancel My Reservation");
            System.out.println("5. My Waitlist Status");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                System.out.println("Exiting system...");
                break;
            }

            switch (choice) {
                case 1:
                    ReservationStrategy normalStrategy = new NormalReservationStrategy();
                    reservationService.reserveSeat(loggedInUserId, normalStrategy, scanner);
                    break;

                case 2:
                    ReservationStrategy priorityStrategy = new PriorityReservationStrategy();
                    reservationService.reserveSeat(loggedInUserId, priorityStrategy, scanner);
                    break;

                case 3:
                    reservationService.showMyReservations(loggedInUserId, scanner);
                    break;

                case 4:
                    reservationService.cancelReservation(loggedInUserId, scanner);
                    break;

                case 5:
                    reservationService.showWaitlistStatus(loggedInUserId);
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}