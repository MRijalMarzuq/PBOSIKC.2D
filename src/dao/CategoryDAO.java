package dao;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public void save(Category category) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, category.getName());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Category with name '" + category.getName() + "' already exists!");
            }
        }

        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
        }
    }

    public List<Category> getAll() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return categories;
    }

    public void update(Category category) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM categories WHERE name = ? AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, category.getName());
            checkStmt.setInt(2, category.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Category with name '" + category.getName() + "' already exists for another ID!");
            }
        }

        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update category: Category not found");
            }
        }
    }

    public void delete(int categoryId) throws SQLException {
        if (isCategoryUsed(categoryId)) {
            throw new SQLException("Cannot delete category: Category is used by one or more books");
        }
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to delete category: Category not found");
            }
        }
    }

    public Category getById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }

    public boolean isCategoryUsed(int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}