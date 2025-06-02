package dao;

import model.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    public void save(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.executeUpdate();
        }
    }

    public List<Member> getAll() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(new Member(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
        }
        return members;
    }

    // Metode untuk klik tabel danmemanggil Nama dan 
    public Member getById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Member(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
            return null;
        }
    }

    // Metode untuk update
    public void update(Member member) throws SQLException {
        String sql = "UPDATE members SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setInt(3, member.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update member: Member not found");
            }
        }
    }

    // Metode untuk mnghapus
    public void delete(int memberId) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to delete member: Member not found");
            }
        }
    }

    // Metode untuk pencarian berdasarkan email
    public List<Member> searchByEmail(String email) throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE email LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + email + "%"); // Pencarian partial match
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(new Member(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
                }
            }
        }
        return members;
    }
}