package model;

public class Fine {
    private int id;
    private int loanId;
    private double amount;
    private String status;

    public Fine(int id, int loanId, double amount, String status) {
        this.id = id;
        this.loanId = loanId;
        this.amount = amount;
        this.status = status;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}