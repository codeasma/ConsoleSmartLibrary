package com.smartlibrary.strategy;

import java.time.LocalDate;

public class PriorityStudentBorrowStrategy implements BorrowStrategy {
    @Override
    public LocalDate calculateDueDate(LocalDate borrowDate) {
        return borrowDate.plusDays(21);
    }

    @Override
    public String getDescription() {
        return "Priority student: 21-day borrowing period";
    }
}
