package com.smartlibrary.observer;

/**
 * Observer Pattern – Observer Interface
 */
public interface Observer {

    /**
     * Called by the Subject when a relevant event occurs.
     *
     * @param message A human-readable notification message (e.g. "A seat is now available for you!")
     */
    void update(String message);
}
