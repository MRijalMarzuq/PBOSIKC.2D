package gui;

import dao.BookDAO;
import dao.CategoryDAO;
import model.Book;
import model.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BookViewFrame extends JFrame {
    private JComboBox<String> categoryFilterComboBox;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;

    public BookViewFrame() {
        setTitle("View Books");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout());

        // Panel untuk filter
        JPanel topPanel = new JPanel(new FlowLayout());
        categoryFilterComboBox = new JComboBox<>();
        categoryFilterComboBox.addItem("All"); // Opsi untuk menampilkan semua kategori
        topPanel.add(new JLabel("Filter by Category Name:"));
        topPanel.add(categoryFilterComboBox);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Nama Buku", "Penulis", "Kategori"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        // Event listener untuk filter
        categoryFilterComboBox.addActionListener(e -> filterBooks());

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load categories and books
        loadCategories();
        filterBooks();
        setVisible(true);
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAll();
            for (Category category : categories) {
                categoryFilterComboBox.addItem(category.getName());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void filterBooks() {
        try {
            String selectedCategoryName = (String) categoryFilterComboBox.getSelectedItem();
            List<Book> books = bookDAO.getAll();
            tableModel.setRowCount(0);
            for (Book book : books) {
                Category category = categoryDAO.getById(book.getCategoryId());
                String categoryName = category != null ? category.getName() : "Unknown";
                if ("All".equals(selectedCategoryName) || categoryName.equals(selectedCategoryName)) {
                    tableModel.addRow(new Object[]{
                        book.getName(),
                        book.getAuthor(),
                        categoryName
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }
}