package gui;

import dao.LoanDAO;
import dao.FineDAO;
import dao.MemberDAO;
import dao.BookDAO;
import model.Loan;
import model.Fine;
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
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MemberProfileFrame extends JFrame {
    private JTextField memberIdField, memberNameField, activeLoansField, totalFineField;
    private JTable loanHistoryTable;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;
    private LoanDAO loanDAO;
    private FineDAO fineDAO;
    private BookDAO bookDAO;

    public MemberProfileFrame() {
        setTitle("Member Profile");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        memberDAO = new MemberDAO();
        loanDAO = new LoanDAO();
        fineDAO = new FineDAO();
        bookDAO = new BookDAO();

        setLayout(new BorderLayout());

        // Panel atas untuk input dan info
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Member ID:"), gbc);
        gbc.gridx = 1;
        memberIdField = new JTextField(10);
        topPanel.add(memberIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Member Name:"), gbc);
        gbc.gridx = 1;
        memberNameField = new JTextField(20);
        memberNameField.setEditable(false);
        topPanel.add(memberNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Active Loans:"), gbc);
        gbc.gridx = 1;
        activeLoansField = new JTextField(10);
        activeLoansField.setEditable(false);
        topPanel.add(activeLoansField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("Total Unpaid Fine (Rp):"), gbc);
        gbc.gridx = 1;
        totalFineField = new JTextField(10);
        totalFineField.setEditable(false);
        topPanel.add(totalFineField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton loadButton = new JButton("Load Profile");
        JButton printButton = new JButton("Print to PDF");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loadButton);
        buttonPanel.add(printButton);
        topPanel.add(buttonPanel, gbc);

        // Table untuk riwayat peminjaman
        tableModel = new DefaultTableModel(new String[]{"Loan ID", "Nama Buku", "Loan Date", "Due Date", "Return Date", "Status Denda"}, 0);
        loanHistoryTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(loanHistoryTable);

        // Event listener
        loadButton.addActionListener(e -> loadProfile());
        printButton.addActionListener(e -> printToPDF());

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadProfile() {
        try {
            int memberId = Integer.parseInt(memberIdField.getText().trim());
            if (memberId <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid member ID!");
                return;
            }
            Member member = memberDAO.getById(memberId);
            if (member == null) {
                JOptionPane.showMessageDialog(this, "Member not found!");
                clearFields();
                return;
            }
            memberNameField.setText(member.getName());

            List<Loan> loans = loanDAO.getAll();
            int activeLoans = 0;
            double totalFine = 0.0;
            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Loan loan : loans) {
                if (loan.getMemberId() == memberId) {
                    if (loan.getReturnDate() == null) activeLoans++;
                    String bookName = bookDAO.getById(loan.getBookId()) != null ? bookDAO.getById(loan.getBookId()).getName() : "Unknown";
                    Fine fine = fineDAO.getAll().stream()
                        .filter(f -> f.getLoanId() == loan.getId() && "UNPAID".equals(f.getStatus()))
                        .findFirst().orElse(null);
                    String fineStatus = fine != null ? "Rp " + String.format("%.2f", fine.getAmount()) : "No Fine";
                    tableModel.addRow(new Object[]{
                        loan.getId(),
                        bookName,
                        dateFormat.format(loan.getLoanDate()),
                        dateFormat.format(loan.getDueDate()),
                        loan.getReturnDate() != null ? dateFormat.format(loan.getReturnDate()) : "Not Returned",
                        fineStatus
                    });
                    if (fine != null) totalFine += fine.getAmount();
                }
            }
            activeLoansField.setText(String.valueOf(activeLoans));
            totalFineField.setText(String.format("%.2f", totalFine));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric member ID!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    private void printToPDF() {
        Document document = new Document();
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            String filename = "MemberProfile_" + memberIdField.getText() + "_" + timestamp + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Member Profile Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
            document.add(new Paragraph("Member ID: " + memberIdField.getText()));
            document.add(new Paragraph("Member Name: " + memberNameField.getText()));
            document.add(new Paragraph("Active Loans: " + activeLoansField.getText()));
            document.add(new Paragraph("Total Unpaid Fine (Rp): " + totalFineField.getText()));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1, 2, 1.5f, 1.5f, 1.5f, 1.5f});

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] headers = {"Loan ID", "Nama Buku", "Loan Date", "Due Date", "Return Date", "Status Denda"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

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

    private void clearFields() {
        memberNameField.setText("");
        activeLoansField.setText("");
        totalFineField.setText("");
        tableModel.setRowCount(0);
    }
}