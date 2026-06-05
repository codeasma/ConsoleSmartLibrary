import java.time.LocalDate;

public class NormalStudentBorrowStrategy implements BorrowStrategy {
    @Override
    public LocalDate calculateDueDate(LocalDate borrowDate) {
        return borrowDate.plusDays(14);
    }

    @Override
    public String getDescription() {
        return "Normal student: 14-day borrowing period";
    }
}