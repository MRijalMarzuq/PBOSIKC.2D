package gui;

import dao.BookDAO;
import dao.FineDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import model.Book;
import model.Fine;
import model.Loan;
import model.Member;
import report.FineReportGenerator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageFineFrame extends JFrame {
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable fineTable;
    private FineDAO fineDAO;
    private LoanDAO loanDAO;
    private MemberDAO memberDAO;
    private BookDAO bookDAO;
    private int selectedFineId = -1;

    public ManageFineFrame() {
        setTitle("Manage Fines");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        fineDAO = new FineDAO();
        loanDAO = new LoanDAO();
        memberDAO = new MemberDAO();
        bookDAO = new BookDAO();

        // Panel utama dengan BorderLayout
        setLayout(new BorderLayout());

        // Panel atas untuk search dan tombol-tombol
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Panel search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by member name");
        searchPanel.add(new JLabel("Search Member Name:"));
        searchPanel.add(searchField);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateStatusButton = new JButton("Mark as Paid");
        JButton deleteButton = new JButton("Delete Fine");
        JButton generateReportButton = new JButton("Generate Fine Report");
        
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(generateReportButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Tabel Denda
        tableModel = new DefaultTableModel(new String[]{"ID", "Loan ID", "Member Name", "Member Email", "Book Title", "Amount", "Status"}, 0);
        fineTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(fineTable);

        // Event listener untuk pencarian real-time
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterFines();
            }
        });

        // MouseListener untuk memilih baris di tabel
        fineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = fineTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    selectedFineId = (int) tableModel.getValueAt(row, 0);
                }
            }
        });

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Event Listeners
        updateStatusButton.addActionListener(e -> markAsPaid());
        deleteButton.addActionListener(e -> deleteFine());
        generateReportButton.addActionListener(e -> {
            try {
                generateFineReport();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ManageFineFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        loadFines();
        setVisible(true);
    }

    private void loadFines() {
        try {
            tableModel.setRowCount(0);
            for (Fine fine : fineDAO.getAll()) {
                Loan loan = loanDAO.getById(fine.getLoanId());
                Member member = loan != null ? memberDAO.getById(loan.getMemberId()) : null;
                Book book = loan != null ? bookDAO.getById(loan.getBookId()) : null;
                tableModel.addRow(new Object[]{
                    fine.getId(),
                    fine.getLoanId(),
                    member != null ? member.getName() : "Unknown",
                    member != null ? member.getEmail() : "Unknown",
                    book != null ? book.getName() : "Unknown",
                    fine.getAmount(),
                    fine.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading fines: " + e.getMessage());
        }
    }

    private void filterFines() {
        String searchText = searchField.getText().trim().toLowerCase();
        try {
            tableModel.setRowCount(0);
            for (Fine fine : fineDAO.getAll()) {
                Loan loan = loanDAO.getById(fine.getLoanId());
                Member member = loan != null ? memberDAO.getById(loan.getMemberId()) : null;
                Book book = loan != null ? bookDAO.getById(loan.getBookId()) : null;
                
                String memberName = member != null ? member.getName().toLowerCase() : "unknown";
                
                // Filter berdasarkan nama member
                if (searchText.isEmpty() || memberName.contains(searchText)) {
                    tableModel.addRow(new Object[]{
                        fine.getId(),
                        fine.getLoanId(),
                        member != null ? member.getName() : "Unknown",
                        member != null ? member.getEmail() : "Unknown",
                        book != null ? book.getName() : "Unknown",
                        fine.getAmount(),
                        fine.getStatus()
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering fines: " + e.getMessage());
        }
    }

    private void markAsPaid() {
        if (selectedFineId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to update!");
            return;
        }
        try {
            Fine fine = fineDAO.getById(selectedFineId);
            if (fine != null && "UNPAID".equals(fine.getStatus())) {
                fineDAO.updateStatus(selectedFineId, "PAID");
                JOptionPane.showMessageDialog(this, "Fine marked as paid!");
                // Refresh tampilan dengan mempertahankan filter
                if (searchField.getText().trim().isEmpty()) {
                    loadFines();
                } else {
                    filterFines();
                }
            } else if (fine != null && "PAID".equals(fine.getStatus())) {
                JOptionPane.showMessageDialog(this, "Fine is already paid!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating fine status: " + e.getMessage());
        }
    }

    private void deleteFine() {
        if (selectedFineId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to delete!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this fine?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                fineDAO.delete(selectedFineId);
                JOptionPane.showMessageDialog(this, "Fine deleted successfully!");
                // Refresh tampilan dengan mempertahankan filter
                if (searchField.getText().trim().isEmpty()) {
                    loadFines();
                } else {
                    filterFines();
                }
                selectedFineId = -1;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting fine: " + e.getMessage());
            }
        }
    }

    private void generateFineReport() throws FileNotFoundException {
        try {
            FineReportGenerator generator = new FineReportGenerator();
            String outputPath = "FineReport_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            generator.generateFineReport(outputPath);
            JOptionPane.showMessageDialog(this, "Laporan denda berhasil dibuat: " + outputPath);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
        }
    }
}