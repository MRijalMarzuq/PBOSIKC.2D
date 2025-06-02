package report;

import java.sql.*;
import dao.DatabaseConnection;

public class LoanReport implements Reportable {
    @Override
    public void generateReport() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT l.id, m.name, b.title, l.loan_date, l.return_date " +
                     "FROM loans l JOIN members m ON l.member_id = m.id JOIN books b ON l.book_id = b.id")) {
            while (rs.next()) {
                System.out.println("Loan ID: " + rs.getInt("id") + ", Member: " + rs.getString("name") +
                        ", Book: " + rs.getString("title") + ", Loan Date: " + rs.getDate("loan_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}