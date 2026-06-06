package com.smartlibrary.manager;

import java.time.LocalDate;
import java.util.*;

public class BookManager {
    private List<Book> books = new ArrayList<>();
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    // Search by title or author
    public List<Book> searchBook(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public BorrowRecord borrowBook(User user, String bookId, BorrowStrategy strategy) {
        for (Book b : books) {
            if (b.getId().equals(bookId) && b.isAvailable()) {
                b.setAvailable(false);
                LocalDate today = LocalDate.now();
                LocalDate dueDate = strategy.calculateDueDate(today);
                BorrowRecord record = new BorrowRecord(user, b, today, dueDate);
                borrowRecords.add(record);
                System.out.println("Book borrowed! Due date: " + dueDate);
                System.out.println("Policy: " + strategy.getDescription());
                return record;
            }
        }
        System.out.println("Book not available.");
        return null;
    }

    public void returnBook(User user, String bookId) {
        BorrowRecord toRemove = null;
        for (BorrowRecord r : borrowRecords) {
            if (r.getBook().getId().equals(bookId) && r.getUser().getId().equals(user.getId())) {
                LocalDate today = LocalDate.now();
                int fine = r.calculateFine(today);
                r.getBook().setAvailable(true);
                toRemove = r;
                if (fine > 0) {
                    System.out.println("Late return! Fine: " + fine + " TL");
                } else {
                    System.out.println("Book returned on time. No fine.");
                }
                break;
            }
        }
        if (toRemove != null) borrowRecords.remove(toRemove);
        else System.out.println("No borrow record found for this book.");
    }

    public List<Book> getAllBooks() { return books; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
}