package report;

import dao.DatabaseConnection;
import java.sql.*;

public class PopularBookReport implements Reportable {
    @Override
    public void generateReport() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT b.title, COUNT(l.id) as loan_count " +
                     "FROM books b LEFT JOIN loans l ON b.id = l.book_id " +
                     "GROUP BY b.id, b.title ORDER BY loan_count DESC LIMIT 10")) {
            while (rs.next()) {
                System.out.println("Book: " + rs.getString("title") + 
                                   ", Loan Count: " + rs.getInt("loan_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}