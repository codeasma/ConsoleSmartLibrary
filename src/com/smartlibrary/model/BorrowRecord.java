import java.time.LocalDate;

public class BorrowRecord {
    private User user;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public BorrowRecord(User user, Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public User getUser() { return user; }
    public Book getBook() { return book; }
    public LocalDate getDueDate() { return dueDate; }

    public int calculateFine(LocalDate returnDate) {
        if (returnDate.isAfter(dueDate)) {
            return (int) (returnDate.toEpochDay() - dueDate.toEpochDay()) * 5; // 5 TL per day
        }
        return 0;
    }
}