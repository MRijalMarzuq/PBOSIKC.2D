package gui;

import dao.BookDAO;
import model.Book;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SearchFrame extends JFrame {
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private BookDAO bookDAO;

    public SearchFrame() {
        setTitle("Search Books");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookDAO = new BookDAO();

        // Panel utama dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Jarak antar elemen
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Search Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Search Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        searchField = new JTextField(15);
        formPanel.add(searchField, gbc);

        // Tombol Search
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JButton searchButton = new JButton("Search");
        formPanel.add(searchButton, gbc);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category ID", "Stock"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Event Listeners
        searchButton.addActionListener(e -> searchBooks());

        setVisible(true);
    }

    private void searchBooks() {
        try {
            String query = searchField.getText().trim();
            List<Book> books;
            if (query.isEmpty()) {
                books = bookDAO.getAll(); // Jika field kosong, tampilkan semua buku
            } else {
                books = bookDAO.searchByTitle(query); // Cari berdasarkan judul
            }
            tableModel.setRowCount(0);
            for (Book book : books) {
                tableModel.addRow(new Object[]{book.getId(), book.getName(), book.getAuthor(),
                        book.getCategoryId(), book.getStock()});
            }
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books found with title containing: " + query);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage());
        }
    }
}