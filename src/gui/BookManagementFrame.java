package gui;

import dao.BookDAO;
import dao.CategoryDAO;
import model.Book;
import model.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookManagementFrame extends JFrame {
    private JTextField titleField, authorField, stockField;
    private JComboBox<Category> categoryComboBox;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private Map<Integer, String> categoryMap;
    private int selectedBookId = -1;

    public BookManagementFrame() {
        setTitle("Manage Books");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();
        categoryMap = new HashMap<>();

        // Panel utama dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        // Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        authorField = new JTextField(20);
        formPanel.add(authorField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        categoryComboBox = new JComboBox<>();
        loadCategories();
        formPanel.add(categoryComboBox, gbc);

        // Stock
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Stock:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        stockField = new JTextField(20);
        formPanel.add(stockField, gbc);

        // Tombol Add Book
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JButton addButton = new JButton("Add Book");
        formPanel.add(addButton, gbc);

        // Tombol Update Book
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JButton updateButton = new JButton("Update Book");
        formPanel.add(updateButton, gbc);

        // Tombol Delete Book
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JButton deleteButton = new JButton("Delete Book");
        formPanel.add(deleteButton, gbc);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Stock"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        // Tambahkan MouseListener untuk klik pada baris tabel
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    selectedBookId = (int) tableModel.getValueAt(row, 0);
                    try {
                        Book book = bookDAO.getById(selectedBookId);
                        if (book != null) {
                            titleField.setText(book.getName());
                            authorField.setText(book.getAuthor());
                            stockField.setText(String.valueOf(book.getStock()));
                            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                                Category category = categoryComboBox.getItemAt(i);
                                if (category.getId() == book.getCategoryId()) {
                                    categoryComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(BookManagementFrame.this,
                                "Error retrieving book details: " + ex.getMessage());
                    }
                }
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Event Listeners
        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        loadBooks();

        setVisible(true);
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAll();
            categoryComboBox.removeAllItems(); // Hapus item lama untuk mencegah duplikat
            for (Category category : categories) {
                categoryComboBox.addItem(category);
                categoryMap.put(category.getId(), category.getName()); // Sinkronkan dengan categoryMap
            }
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No categories found. Please add a category first.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void addBook() {
        try {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select a category!");
                return;
            }
            Book book = new Book(0, titleField.getText(), authorField.getText(),
                    selectedCategory.getId(), Integer.parseInt(stockField.getText()));
            bookDAO.save(book);
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            clearForm();
            loadBooks();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateBook() {
        if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to update!");
            return;
        }
        try {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select a category!");
                return;
            }
            Book book = new Book(selectedBookId, titleField.getText(), authorField.getText(),
                    selectedCategory.getId(), Integer.parseInt(stockField.getText()));
            bookDAO.update(book);
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            clearForm();
            loadBooks();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to delete!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookDAO.delete(selectedBookId);
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                clearForm();
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadBooks() {
        try {
            List<Book> books = bookDAO.getAll();
            tableModel.setRowCount(0);
            for (Book book : books) {
                String categoryName = categoryMap.getOrDefault(book.getCategoryId(), "Unknown");
                tableModel.addRow(new Object[]{book.getId(), book.getName(), book.getAuthor(),
                        categoryName, book.getStock()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        stockField.setText("");
        categoryComboBox.setSelectedIndex(-1);
        selectedBookId = -1;
    }
}