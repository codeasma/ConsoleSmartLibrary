package com.smartlibrary.strategy;

import java.time.LocalDate;

public interface BorrowStrategy {
    LocalDate calculateDueDate(LocalDate borrowDate);
    String getDescription();
}
