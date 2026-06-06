package com.smartlibrary.observer;

/**
 * Observer Pattern – Subject Interface
 *
 * Any class that wants to send notifications must implement this interface.
 * In our system, NotificationService will implement this.
 */
public interface Subject {

    /**
     * Register an observer to receive notifications.
     *
     * @param studentId The ID of the student to associate with this observer
     * @param observer  The observer instance (usually the student object itself)
     */
    void registerObserver(String studentId, Observer observer);

    /**
     * Remove an observer so they no longer receive notifications.
     *
     * @param studentId The ID of the student to remove
     */
    void removeObserver(String studentId);

    /**
     * Send a notification to a specific observer by student ID.
     *
     * @param studentId The ID of the student to notify
     * @param message   The notification message to deliver
     */
    void notifyObserver(String studentId, String message);

    /**
     * Broadcast a notification to ALL registered observers.
     * Useful for system-wide announcements.
     *
     * @param message The notification message to deliver to everyone
     */
    void notifyAllObservers(String message);
}

