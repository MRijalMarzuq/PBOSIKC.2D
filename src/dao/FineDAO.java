package dao;

import model.Fine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {
    public void save(Fine fine) throws SQLException {
        String sql = "INSERT INTO fines (loan_id, amount, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fine.getLoanId());
            stmt.setDouble(2, fine.getAmount());
            stmt.setString(3, fine.getStatus());
            stmt.executeUpdate();
        }
    }

    public List<Fine> getAll() throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fines.add(new Fine(rs.getInt("id"), rs.getInt("loan_id"), 
                                   rs.getDouble("amount"), rs.getString("status")));
            }
        }
        return fines;
    }

    public Fine getById(int id) throws SQLException {
        String sql = "SELECT * FROM fines WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Fine(rs.getInt("id"), rs.getInt("loan_id"), 
                                    rs.getDouble("amount"), rs.getString("status"));
                }
            }
        }
        return null;
    }

    public void updateStatus(int fineId, String newStatus) throws SQLException {
        String sql = "UPDATE fines SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, fineId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update fine: Fine not found");
            }
        }
    }

    public void delete(int fineId) throws SQLException {
        String sql = "DELETE FROM fines WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to delete fine: Fine not found");
            }
        }
    }

    public Fine findByLoanId(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}