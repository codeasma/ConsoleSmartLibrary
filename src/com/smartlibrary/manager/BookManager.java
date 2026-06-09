package com.smartlibrary.manager;

import com.smartlibrary.service.NotificationService;
import java.time.LocalDate;
import java.util.*;
import com.smartlibrary.model.Book;
import com.smartlibrary.model.BorrowRecord;
import com.smartlibrary.model.User;
import com.smartlibrary.strategy.BorrowStrategy;

public class BookManager {
    private NotificationService notificationService;
    private List<Book> books = new ArrayList<>();
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public BookManager() {
        this(null);
    }

    public BookManager(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

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

                if (notificationService != null) {
                    notificationService.notifyBookBorrowed(user.getId(), b.getTitle(), dueDate.toString());
                    System.out.println("Borrow details sent as notification.");
                }

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

                if (notificationService != null) {
                    notificationService.notifyBookReturned(user.getId(), r.getBook().getTitle());
                    System.out.println("Return details sent as notification.");
                }

                break;
            }
        }

        if (toRemove != null) borrowRecords.remove(toRemove);
        else System.out.println("No borrow record found for this book.");
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }
}