package dao;

import model.Loan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    public void save(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (member_id, book_id, loan_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, loan.getMemberId());
            stmt.setInt(2, loan.getBookId());
            stmt.setDate(3, new java.sql.Date(loan.getLoanDate().getTime()));
            stmt.setDate(4, new java.sql.Date(loan.getDueDate().getTime()));
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                loan.setId(rs.getInt(1));
            }
        }
    }

    public List<Loan> getAll() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    rs.getInt("book_id"),
                    rs.getDate("loan_date")
                );
                loan.setDueDate(rs.getDate("due_date"));
                loan.setReturnDate(rs.getDate("return_date"));
                loans.add(loan);
            }
        }
        return loans;
    }

    public Loan getById(int id) throws SQLException {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan(
                        rs.getInt("id"),
                        rs.getInt("member_id"),
                        rs.getInt("book_id"),
                        rs.getDate("loan_date")
                    );
                    loan.setDueDate(rs.getDate("due_date"));
                    loan.setReturnDate(rs.getDate("return_date"));
                    return loan;
                }
            }
        }
        return null;
    }

    public void update(Loan loan) throws SQLException {
        String sql = "UPDATE loans SET return_date = ?, due_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, loan.getReturnDate() != null ? new java.sql.Date(loan.getReturnDate().getTime()) : null);
            stmt.setDate(2, loan.getDueDate() != null ? new java.sql.Date(loan.getDueDate().getTime()) : null);
            stmt.setInt(3, loan.getId());
            stmt.executeUpdate();
        }
    }
}