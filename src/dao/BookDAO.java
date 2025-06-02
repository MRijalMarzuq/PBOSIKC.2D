package dao;

import model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public void save(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, category_id, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getCategoryId());
            stmt.setInt(4, book.getStock());
            stmt.executeUpdate();
        }
    }

    public List<Book> getAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"),
                        rs.getString("author"), rs.getInt("category_id"), rs.getInt("stock")));
            }
        }
        return books;
    }

    public Book getById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book(rs.getInt("id"), rs.getString("title"),
                        rs.getString("author"), rs.getInt("category_id"), rs.getInt("stock"));
            }
            return null;
        }
    }

    public List<Book> searchByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + title + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(rs.getInt("id"), rs.getString("title"),
                            rs.getString("author"), rs.getInt("category_id"), rs.getInt("stock")));
                }
            }
        }
        return books;
    }

    public void decreaseStock(int bookId) throws SQLException {
        String sql = "UPDATE books SET stock = stock - 1 WHERE id = ? AND stock > 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to decrease stock: Book not found or stock is already 0");
            }
        }
    }

    public void increaseStock(int bookId) throws SQLException {
        String sql = "UPDATE books SET stock = stock + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to increase stock: Book not found");
            }
        }
    }

    // Metode baru untuk update buku
    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, category_id = ?, stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getCategoryId());
            stmt.setInt(4, book.getStock());
            stmt.setInt(5, book.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update book: Book not found");
            }
        }
    }

    // Metode baru untuk hapus buku
    public void delete(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to delete book: Book not found");
            }
        }
    }
}