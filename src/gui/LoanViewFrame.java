package gui;

import dao.LoanDAO;
import dao.MemberDAO;
import dao.BookDAO;
import model.Loan;
import model.Member;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoanViewFrame extends JFrame {
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable loanTable;
    private LoanDAO loanDAO;
    private MemberDAO memberDAO;
    private BookDAO bookDAO;

    public LoanViewFrame() {
        setTitle("View Loans");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loanDAO = new LoanDAO();
        memberDAO = new MemberDAO();
        bookDAO = new BookDAO();

        // Panel utama dengan BorderLayout
        setLayout(new BorderLayout());

        // Panel untuk search dan tombol export
        JPanel topPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by member name");
        JButton exportButton = new JButton("Export to PDF");
        topPanel.add(new JLabel("Search Member Name:"));
        topPanel.add(searchField);
        topPanel.add(exportButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID Pesanan", "Nama Peminjam", "Email Peminjam", "Nama Buku", "Loan Date", "Due Date", "Return Date"}, 0);
        loanTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(loanTable);

        // Tambahkan event listener untuk pencarian real-time
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterLoans();
            }
        });

        // Tambahkan event listener untuk tombol export
        exportButton.addActionListener(e -> exportToPDF());

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load data awal
        loadLoans();
        setVisible(true);
    }

    private void loadLoans() {
        try {
            List<Loan> loans = loanDAO.getAll();
            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Loan loan : loans) {
                Member member = memberDAO.getById(loan.getMemberId());
                String memberName = member != null ? member.getName() : "Unknown";
                String memberEmail = member != null ? member.getEmail() : "Unknown";
                String bookName = bookDAO.getById(loan.getBookId()) != null ? bookDAO.getById(loan.getBookId()).getName() : "Unknown";
                tableModel.addRow(new Object[]{
                    loan.getId(),
                    memberName,
                    memberEmail,
                    bookName,
                    dateFormat.format(loan.getLoanDate()),
                    dateFormat.format(loan.getDueDate()),
                    loan.getReturnDate() != null ? dateFormat.format(loan.getReturnDate()) : "Not Returned"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading loans: " + e.getMessage());
        }
    }

    private void filterLoans() {
        String searchText = searchField.getText().trim().toLowerCase();
        try {
            List<Loan> loans = loanDAO.getAll();
            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Loan loan : loans) {
                Member member = memberDAO.getById(loan.getMemberId());
                String memberName = member != null ? member.getName().toLowerCase() : "unknown";
                if (searchText.isEmpty() || memberName.contains(searchText)) {
                    String memberEmail = member != null ? member.getEmail() : "Unknown";
                    String bookName = bookDAO.getById(loan.getBookId()) != null ? bookDAO.getById(loan.getBookId()).getName() : "Unknown";
                    tableModel.addRow(new Object[]{
                        loan.getId(),
                        member != null ? member.getName() : "Unknown",
                        memberEmail,
                        bookName,
                        dateFormat.format(loan.getLoanDate()),
                        dateFormat.format(loan.getDueDate()),
                        loan.getReturnDate() != null ? dateFormat.format(loan.getReturnDate()) : "Not Returned"
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering loans: " + e.getMessage());
        }
    }

    private void exportToPDF() {
        Document document = new Document();
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            String filename = "LoanReport_" + timestamp + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Judul
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Loan Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Tanggal pembuatan
            document.add(new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
            document.add(new Paragraph(" "));

            // Tabel
            PdfPTable pdfTable = new PdfPTable(7); // 7 kolom sesuai tabel dengan email
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1, 2, 2, 2, 1.5f, 1.5f, 1.5f});

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] headers = {"ID Pesanan", "Nama Peminjam", "Email Peminjam", "Nama Buku", "Loan Date", "Due Date", "Return Date"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Isi tabel dari data yang ditampilkan (termasuk filter)
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    String value = tableModel.getValueAt(row, col).toString();
                    PdfPCell cell = new PdfPCell(new Phrase(value));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "PDF exported successfully as " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
        }
    }
}