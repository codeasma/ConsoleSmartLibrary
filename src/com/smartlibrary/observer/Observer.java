package com.smartlibrary.observer;

/**
 * Observer Pattern – Observer Interface
 *
 * Any class that wants to receive notifications must implement this interface.
 * In our system, this will typically represent a Student waiting for a seat
 * or a book return reminder.
 */
public interface Observer {

    /**
     * Called by the Subject when a relevant event occurs.
     *
     * @param message A human-readable notification message (e.g. "A seat is now available for you!")
     */
    void update(String message);
}
