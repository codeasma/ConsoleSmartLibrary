package com.smartlibrary.waitlist;

import java.util.LinkedList;
import java.util.Queue;

public class Waitlist {
    private Queue<String> waitingStudents;

    public Waitlist() {
        this.waitingStudents = new LinkedList<>();
    }

    public void addStudent(String userId) {
        if (waitingStudents.contains(userId)) {
            System.out.println("You are already in the waitlist.");
            return;
        }

        waitingStudents.add(userId);
        System.out.println("You have been added to the waitlist.");
    }

    public void addStudentSilently(String userId) {
        if (!waitingStudents.contains(userId)) {
            waitingStudents.add(userId);
        }
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

    public boolean containsStudent(String userId) {
        return waitingStudents.contains(userId);
    }

    public int getStudentPosition(String userId) {
        int position = 1;

        for (String waitingStudent : waitingStudents) {
            if (waitingStudent.equals(userId)) {
                return position;
            }
            position++;
        }

        return -1;
    }
}
