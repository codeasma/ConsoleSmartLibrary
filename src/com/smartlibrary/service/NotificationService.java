package com.smartlibrary.service;

import com.smartlibrary.observer.Observer;
import com.smartlibrary.observer.Subject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observer Pattern – Concrete Subject
 * Events handled:
 *   1. Seat becomes available → notify next waitlisted student
 *   2. Book return reminder   → notify a student before their due date
 *   3. Reservation cancelled  → notify the affected student
 */
public class NotificationService implements Subject {

    // Maps studentId → their Observer instance (usually the student console listener)
    private Map<String, Observer> observers = new HashMap<>();

    // Stores all sent notifications for admin dashboard display
    private List<String> notificationLog = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Subject interface implementation
    // -------------------------------------------------------------------------

    @Override
    public void registerObserver(String studentId, Observer observer) {
        observers.put(studentId, observer);
    }

    @Override
    public void removeObserver(String studentId) {
        observers.remove(studentId);
    }

    /**
     * Notify a specific student by their ID.
     * Used for: waitlist seat assignment, book reminders, reservation changes.
     */
    @Override
    public void notifyObserver(String studentId, String message) {
        Observer observer = observers.get(studentId);
        if (observer != null) {
            observer.update(message);
            log(studentId, message);
        } else {
            // Student not registered as observer — still log it
            log(studentId, message);
        }
    }

    /**
     * Broadcast a message to all registered observers.
     * Used for: system-wide announcements (e.g. "Library closing in 30 minutes").
     */
    @Override
    public void notifyAllObservers(String message) {
        for (Map.Entry<String, Observer> entry : observers.entrySet()) {
            entry.getValue().update(message);
            log(entry.getKey(), message);
        }
    }

    // -------------------------------------------------------------------------
    // Domain-specific notification methods
    // -------------------------------------------------------------------------

    /**
     * Called by ReservationService when a seat opens up and a waitlisted
     * student is next in line.
     */
    public void notifySeatAvailable(String studentId) {
        notifyObserver(studentId,
            "[SEAT NOTIFICATION] A seat is now available for you! " +
            "Please arrive promptly to claim your reservation.");
    }

    public void notifySeatAvailable(String studentId, int seatId) {
        notifyObserver(studentId,
                "Notification sent to " + studentId + ": Seat " + seatId +
                        " is now available. Do you want to reserve it?");
    }

    public void notifyBookBorrowed(String studentId, String bookTitle, LocalDate dueDate, String policy) {
        notifyObserver(studentId,
                "[BOOK NOTIFICATION] Book borrowed successfully.\n" +
                        "Book: " + bookTitle + "\n" +
                        "Borrow duration: 14 days.");
    }

    public void notifyBookReturned(String studentId, String bookTitle, int fine) {
        notifyObserver(studentId,
                "[BOOK NOTIFICATION] Book returned successfully.\n" +
                        "Book: " + bookTitle + "\n" +
                        "The book is now available.");
    }

    public void notifySeatReserved(String studentId, int seatId) {
        notifyObserver(studentId,
                "[RESERVATION NOTIFICATION] Reservation successful.\n" +
                        "Seat ID: " + seatId + "\n" +
                        "Reservation duration: 2 hours.\n" +
                        "This seat expires in 15 minutes.");
    }

    public void notifyReservationCancelled(String studentId) {
        notifyObserver(studentId,
                "[RESERVATION NOTIFICATION] Your reservation has been cancelled.");
    }

    /**
     * Called before a book's due date to remind the student to return it.
     *
     * @param studentId The borrowing student's ID
     * @param bookTitle The title of the borrowed book
     * @param dueDate   The due date of the book
     */
    public void notifyBookReturnReminder(String studentId, String bookTitle, LocalDate dueDate) {
        long daysLeft = LocalDate.now().until(dueDate).getDays();
        String urgency = daysLeft == 0 ? "TODAY" : "in " + daysLeft + " day(s)";
        notifyObserver(studentId,
            "[BOOK REMINDER] \"" + bookTitle + "\" is due " + urgency +
            " (" + dueDate + "). Please return it on time to avoid a fine.");
    }

    /**
     * Called when a reservation is cancelled (by the student or by expiry timeout).
     */
    public void notifyReservationCancelled(String studentId, int reservationId) {
        notifyObserver(studentId,
            "[RESERVATION] Your reservation #" + reservationId +
            " has been cancelled or has expired.");
    }

    // -------------------------------------------------------------------------
    // Admin / logging support
    // -------------------------------------------------------------------------

    private void log(String studentId, String message) {
        String entry = "[" + java.time.LocalDateTime.now() + "] → Student " + studentId + ": " + message;
        notificationLog.add(entry);
    }

    /**
     * Returns all sent notifications — used by the admin dashboard in Main.java.
     */
    public List<String> getNotificationLog() {
        return notificationLog;
    }

    /**
     * Prints all notifications to console — for admin dashboard display.
     */
    public void showNotificationLog() {
        if (notificationLog.isEmpty()) {
            System.out.println("No notifications have been sent yet.");
            return;
        }
        System.out.println("\n--- Notification Log ---");
        for (String entry : notificationLog) {
            System.out.println(entry);
        }
    }
}
