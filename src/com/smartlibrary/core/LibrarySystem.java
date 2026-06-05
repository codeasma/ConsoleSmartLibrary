package com.smartlibrary.core;

import com.smartlibrary.model.Book;
import com.smartlibrary.model.Seat;
import com.smartlibrary.model.User;
import com.smartlibrary.model.StudentType;
import com.smartlibrary.model.BorrowRecord;
import com.smartlibrary.observer.Observer;
import com.smartlibrary.service.NotificationService;
import com.smartlibrary.service.ReservationService;
import com.smartlibrary.manager.BookManager;
import com.smartlibrary.strategy.BorrowStrategy;
import com.smartlibrary.strategy.NormalStudentBorrowStrategy;
import com.smartlibrary.strategy.PriorityStudentBorrowStrategy;
import com.smartlibrary.strategy.ReservationStrategy;
import com.smartlibrary.strategy.NormalReservationStrategy;
import com.smartlibrary.strategy.PriorityReservationStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Singleton Pattern – Central System Instance
 *
 * LibrarySystem is the single entry point for all system operations.
 * It owns and wires together every module:
 *   - BookManager     (Member 1)
 *   - ReservationService (Member 2)
 *   - NotificationService (Member 3 — you)
 *
 * Usage: LibrarySystem system = LibrarySystem.getInstance();
 */
public class LibrarySystem {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static LibrarySystem instance;

    private LibrarySystem() {
        notificationService = new NotificationService();
        reservationService  = new ReservationService(notificationService);
        bookManager         = new BookManager();
        users               = new ArrayList<>();

        seedData();
    }

    public static LibrarySystem getInstance() {
        if (instance == null) {
            instance = new LibrarySystem();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Modules
    // -------------------------------------------------------------------------

    private final NotificationService notificationService;
    private final ReservationService  reservationService;
    private final BookManager         bookManager;
    private final List<User>          users;

    // -------------------------------------------------------------------------
    // Seed data — sample users and books for demo / test cases in Main.java
    // -------------------------------------------------------------------------

    private void seedData() {
        // Sample users
        users.add(new User("Alice",   "S001", StudentType.NORMAL));
        users.add(new User("Bob",     "S002", StudentType.PRIORITY));
        users.add(new User("Charlie", "S003", StudentType.NORMAL));

        // Sample books
        bookManager.addBook(new Book("B001", "Clean Code",                   "Robert C. Martin"));
        bookManager.addBook(new Book("B002", "The Pragmatic Programmer",     "Andrew Hunt"));
        bookManager.addBook(new Book("B003", "Design Patterns",              "Gang of Four"));
        bookManager.addBook(new Book("B004", "Introduction to Algorithms",   "CLRS"));
        bookManager.addBook(new Book("B005", "Software Architecture Patterns","Mark Richards"));

        // Register all seeded users as observers so they receive notifications
        for (User u : users) {
            registerStudentAsObserver(u);
        }
    }

    // -------------------------------------------------------------------------
    // User management
    // -------------------------------------------------------------------------

    /**
     * Look up a user by ID. Returns null if not found.
     */
    public User getUserById(String id) {
        for (User u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    /**
     * Add a new user (e.g. after login/registration) and register them
     * as an observer so they immediately start receiving notifications.
     */
    public void addUser(User user) {
        users.add(user);
        registerStudentAsObserver(user);
    }

    public List<User> getAllUsers() {
        return users;
    }

    // -------------------------------------------------------------------------
    // Observer registration
    // -------------------------------------------------------------------------

    /**
     * Registers a User as an Observer. The Observer implementation here
     * simply prints the notification to the console — appropriate for a
     * console app where the student is the active user.
     */
    private void registerStudentAsObserver(User user) {
        notificationService.registerObserver(user.getId(), message -> {
            System.out.println("\n*** NOTIFICATION for " + user.getName() + " ***");
            System.out.println("  " + message);
            System.out.println("*".repeat(45));
        });
    }

    // -------------------------------------------------------------------------
    // Book operations (delegates to BookManager)
    // -------------------------------------------------------------------------

    public void searchBooks(String keyword) {
        List<Book> results = bookManager.searchBook(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found for: \"" + keyword + "\"");
        } else {
            System.out.println("\n--- Search Results ---");
            for (Book b : results) {
                System.out.println("  [" + b.getId() + "] " + b.getTitle()
                    + " by " + b.getAuthor()
                    + " | " + (b.isAvailable() ? "Available" : "Borrowed"));
            }
        }
    }

    public void borrowBook(User user, String bookId) {
        BorrowStrategy strategy = getBorrowStrategy(user);
        bookManager.borrowBook(user, bookId, strategy);
    }

    public void returnBook(User user, String bookId) {
        bookManager.returnBook(user, bookId);
    }

    public void listAllBooks() {
        List<Book> all = bookManager.getAllBooks();
        System.out.println("\n--- All Books ---");
        for (Book b : all) {
            System.out.println("  [" + b.getId() + "] " + b.getTitle()
                + " by " + b.getAuthor()
                + " | " + (b.isAvailable() ? "Available" : "Borrowed"));
        }
    }

    // -------------------------------------------------------------------------
    // Reservation operations (delegates to ReservationService)
    // -------------------------------------------------------------------------

    public void reserveSeat(User user, Scanner scanner) {
        ReservationStrategy strategy = getReservationStrategy(user);
        reservationService.reserveSeat(user.getId(), strategy, scanner);
    }

    public void showMyReservations(User user, Scanner scanner) {
        reservationService.showMyReservations(user.getId(), scanner);
    }

    public void showWaitlist() {
        reservationService.showWaitlist();
    }

    // -------------------------------------------------------------------------
    // Notification operations
    // -------------------------------------------------------------------------

    /**
     * Checks all active borrow records and sends a reminder to any student
     * whose book is due tomorrow or today. Call this at system startup or
     * from the Main.java menu.
     */
    public void checkAndSendBookReminders() {
        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        boolean   any      = false;

        for (BorrowRecord r : bookManager.getBorrowRecords()) {
            LocalDate due = r.getDueDate();
            if (!due.isBefore(today) && !due.isAfter(tomorrow)) {
                notificationService.notifyBookReturnReminder(
                    r.getUser().getId(),
                    r.getBook().getTitle(),
                    due
                );
                any = true;
            }
        }

        if (!any) {
            System.out.println("No upcoming book due dates to remind.");
        }
    }

    // -------------------------------------------------------------------------
    // Admin dashboard
    // -------------------------------------------------------------------------

    /**
     * Shows seat occupancy — all seats and their current status.
     * Delegates to ReservationService which owns the seat list.
     */
    public void showAdminSeatOccupancy() {
        System.out.println("\n--- Admin: Seat Occupancy ---");
        reservationService.showAllSeats();
    }

    /**
     * Shows all reservation activity (active + past).
     */
    public void showAdminReservationActivity() {
        System.out.println("\n--- Admin: Reservation Activity ---");
        reservationService.showAllReservations();
    }

    /**
     * Shows the full notification log.
     */
    public void showAdminNotificationLog() {
        notificationService.showNotificationLog();
    }

    // -------------------------------------------------------------------------
    // Strategy helpers — auto-selects based on StudentType
    // -------------------------------------------------------------------------

    public BorrowStrategy getBorrowStrategy(User user) {
        if (user.getType() == StudentType.PRIORITY) {
            return new PriorityStudentBorrowStrategy();
        }
        return new NormalStudentBorrowStrategy();
    }

    public ReservationStrategy getReservationStrategy(User user) {
        if (user.getType() == StudentType.PRIORITY) {
            return new PriorityReservationStrategy();
        }
        return new NormalReservationStrategy();
    }
}
