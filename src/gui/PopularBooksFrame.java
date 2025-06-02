package gui;

import dao.BookDAO;
import dao.DatabaseConnection;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                exportToPDF();
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

    private void exportToPDF() {
        Document document = new Document();
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            String filetitle = "PopularBooksReport_" + timestamp + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filetitle));
            document.open();

            // Judul
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Popular Books Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Tanggal pembuatan
            document.add(new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
            document.add(new Paragraph(" "));

            // Tabel
            PdfPTable pdfTable = new PdfPTable(2); // 2 kolom sesuai tabel
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{3, 1}); // Lebar kolom disesuaikan

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] headers = {"Judul Buku", "Banyaknya Dipinjam"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Isi tabel dari data yang ditampilkan
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    String value = tableModel.getValueAt(row, col).toString();
                    PdfPCell cell = new PdfPCell(new Phrase(value));
                    cell.setHorizontalAlignment(col == 1 ? Element.ALIGN_CENTER : Element.ALIGN_LEFT); // Rata tengah untuk kolom kedua
                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "PDF exported successfully as " + filetitle);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
        }
    }
}