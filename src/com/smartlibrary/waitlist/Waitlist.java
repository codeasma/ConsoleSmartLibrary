package com.smartlibrary.waitlist;

import java.util.LinkedList;
import java.util.Queue;

public class Waitlist {
    private Queue<String> waitingStudents;

    public Waitlist() {
        this.waitingStudents = new LinkedList<>();
    }

    public void addStudent(String studentId) {
        waitingStudents.add(studentId);
        System.out.println("Student " + studentId + " has been added to the waitlist.");
    }

    public String getNextStudent() {
        return waitingStudents.poll();
    }

    public boolean isEmpty() {
        return waitingStudents.isEmpty();
    }

    public int getWaitlistSize() {
        return waitingStudents.size();
    }

    @Override
    public String toString() {
        return "Waitlist: " + waitingStudents;
    }
}