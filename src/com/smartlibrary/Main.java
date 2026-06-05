package com.smartlibrary;

import com.smartlibrary.core.LibrarySystem;
import com.smartlibrary.model.User;
import com.smartlibrary.model.StudentType;

import java.util.Scanner;

/**
 * Main entry point for the Smart Library Management System.
 *
 * Responsibilities (Team Member 3):
 *   - Login flow
 *   - Role-based menu (Student / Admin)
 *   - Integrates BookManager (Member 1) and ReservationService (Member 2)
 *     through the LibrarySystem singleton
 *   - Demonstrates all test cases
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Boot the singleton — seeds users, books, seats
        LibrarySystem system = LibrarySystem.getInstance();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     Smart Library Management System      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Check for due-date reminders at startup
        System.out.println("\n[System] Checking for upcoming book due dates...");
        system.checkAndSendBookReminders();

        // Login
        User currentUser = loginMenu(system, scanner);
        if (currentUser == null) {
            System.out.println("Exiting system.");
            scanner.close();
            return;
        }

        System.out.println("\nWelcome, " + currentUser.getName() + "! ["
            + currentUser.getType() + " STUDENT | ID: " + currentUser.getId() + "]");

        // Route to correct menu
        if (isAdmin(currentUser)) {
            adminMenu(system, scanner);
        } else {
            studentMenu(system, currentUser, scanner);
        }

        System.out.println("\nThank you for using the Smart Library System. Goodbye!");
        scanner.close();
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    /**
     * Handles login. Existing users log in by ID; new users register on the spot.
     * Admin access is granted by entering ID "admin".
     */
    private static User loginMenu(LibrarySystem system, Scanner scanner) {
        System.out.println("\n=== Login ===");
        System.out.println("1. Login with existing student ID");
        System.out.println("2. Register as new student");
        System.out.println("3. Login as Admin");
        System.out.println("0. Exit");
        System.out.print("Choose: ");

        int choice = readInt(scanner);

        switch (choice) {
            case 1: {
                System.out.print("Enter your student ID: ");
                String id = scanner.nextLine().trim();
                User user = system.getUserById(id);
                if (user == null) {
                    System.out.println("Student ID not found. Please register first.");
                    return loginMenu(system, scanner);
                }
                return user;
            }
            case 2: {
                System.out.print("Enter your name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter a new student ID: ");
                String id = scanner.nextLine().trim();
                if (system.getUserById(id) != null) {
                    System.out.println("That ID is already taken. Try a different one.");
                    return loginMenu(system, scanner);
                }
                System.out.println("Student type:");
                System.out.println("  1. Normal Student  (borrow: 14 days | reservation: 2 hrs)");
                System.out.println("  2. Priority Student (borrow: 21 days | reservation: 4 hrs)");
                System.out.print("Choose: ");
                int typeChoice = readInt(scanner);
                StudentType type = (typeChoice == 2) ? StudentType.PRIORITY : StudentType.NORMAL;
                User newUser = new User(name, id, type);
                system.addUser(newUser);
                System.out.println("Registration successful!");
                return newUser;
            }
            case 3: {
                // Admin user — not in the student list, handled separately
                return new User("Admin", "admin", StudentType.NORMAL);
            }
            case 0:
                return null;
            default:
                System.out.println("Invalid choice.");
                return loginMenu(system, scanner);
        }
    }

    private static boolean isAdmin(User user) {
        return user.getId().equals("admin");
    }

    // -------------------------------------------------------------------------
    // Student menu
    // -------------------------------------------------------------------------

    private static void studentMenu(LibrarySystem system, User user, Scanner scanner) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║             Student Menu                 ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  BOOKS                                   ║");
            System.out.println("║   1. List all books                      ║");
            System.out.println("║   2. Search books                        ║");
            System.out.println("║   3. Borrow a book                       ║");
            System.out.println("║   4. Return a book                       ║");
            System.out.println("║  SEATS                                   ║");
            System.out.println("║   5. Reserve a seat                      ║");
            System.out.println("║   6. My reservations                     ║");
            System.out.println("║   7. Show waitlist                       ║");
            System.out.println("║  OTHER                                   ║");
            System.out.println("║   8. Check book return reminders         ║");
            System.out.println("║   0. Logout                              ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Choose: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    system.listAllBooks();
                    break;

                case 2:
                    System.out.print("Enter search keyword (title or author): ");
                    String keyword = scanner.nextLine().trim();
                    system.searchBooks(keyword);
                    break;

                case 3:
                    system.listAllBooks();
                    System.out.print("Enter Book ID to borrow: ");
                    String borrowId = scanner.nextLine().trim();
                    system.borrowBook(user, borrowId);
                    break;

                case 4:
                    System.out.print("Enter Book ID to return: ");
                    String returnId = scanner.nextLine().trim();
                    system.returnBook(user, returnId);
                    break;

                case 5:
                    system.reserveSeat(user, scanner);
                    break;

                case 6:
                    system.showMyReservations(user, scanner);
                    break;

                case 7:
                    system.showWaitlist();
                    break;

                case 8:
                    system.checkAndSendBookReminders();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Admin menu
    // -------------------------------------------------------------------------

    private static void adminMenu(LibrarySystem system, Scanner scanner) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║              Admin Dashboard             ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║   1. View seat occupancy                 ║");
            System.out.println("║   2. View reservation activity           ║");
            System.out.println("║   3. View notification log               ║");
            System.out.println("║   4. List all users                      ║");
            System.out.println("║   5. List all books                      ║");
            System.out.println("║   0. Logout                              ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Choose: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    system.showAdminSeatOccupancy();
                    break;

                case 2:
                    system.showAdminReservationActivity();
                    break;

                case 3:
                    system.showAdminNotificationLog();
                    break;

                case 4:
                    System.out.println("\n--- All Registered Users ---");
                    for (User u : system.getAllUsers()) {
                        System.out.println("  [" + u.getId() + "] "
                            + u.getName() + " | " + u.getType());
                    }
                    break;

                case 5:
                    system.listAllBooks();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Safe integer read — handles bad input without crashing.
     */
    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}
