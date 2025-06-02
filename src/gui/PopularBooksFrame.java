package gui;

import dao.BookDAO;
import dao.DatabaseConnection;
import report.PopularBooksReport;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopularBooksFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable popularBooksTable;
    private BookDAO bookDAO;

    public PopularBooksFrame() {
        setTitle("Popular Books");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookDAO = new BookDAO();

        // Panel utama dengan BorderLayout
        setLayout(new BorderLayout());

        // Panel untuk tombol export
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("Export to PDF");
        topPanel.add(exportButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Judul Buku", "Banyaknya Dipinjam"}, 0);
        popularBooksTable = new JTable(tableModel);

        // Mengatur perataan kolom "Banyaknya Dipinjam" ke tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        popularBooksTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(popularBooksTable);

        // Tambahkan event listener untuk tombol export
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PopularBooksReport.exportToPDF(tableModel);
            }
        });

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load data buku terpopuler
        loadPopularBooks();
        setVisible(true);
    }

    private void loadPopularBooks() {
        String sql = "SELECT b.title AS book_title, COUNT(l.id) AS loan_count " +
                     "FROM books b " +
                     "LEFT JOIN loans l ON b.id = l.book_id " +
                     "GROUP BY b.id, b.title " +
                     "ORDER BY loan_count DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                String booktitle = rs.getString("book_title");
                int loanCount = rs.getInt("loan_count");
                tableModel.addRow(new Object[]{booktitle, loanCount});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading popular books: " + e.getMessage());
        }
    }
}