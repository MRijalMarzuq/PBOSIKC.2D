package model;

import java.util.Date;

public class Loan {
    private int id;
    private int memberId;
    private int bookId;
    private Date loanDate;
    private Date dueDate;
    private Date returnDate;
    private Fine fine;

    public Loan(int id, int memberId, int bookId, Date loanDate) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.dueDate = calculateDueDate(loanDate);
        this.returnDate = null;
        this.fine = null;
    }

    private Date calculateDueDate(Date loanDate) {
        long oneDayInMillis = 24 * 60 * 60 * 1000L;
        long sevenDaysInMillis = 7 * oneDayInMillis;
        return new Date(loanDate.getTime() + sevenDaysInMillis);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public Date getLoanDate() { return loanDate; }
    public void setLoanDate(Date loanDate) { this.loanDate = loanDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public Fine getFine() { return fine; }
    public void setFine(Fine fine) { this.fine = fine; }
}