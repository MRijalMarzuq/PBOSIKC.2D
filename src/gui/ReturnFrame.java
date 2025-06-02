package gui;

import dao.FineDAO;
import dao.LoanDAO;
import dao.BookDAO;
import dao.MemberDAO;
import model.Fine;
import model.Loan;
import model.Book;
import model.Member;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReturnFrame extends JFrame {
    private JComboBox<Integer> loanIdComboBox;
    private JTextField memberNameField, bookTitleField, fineAmountField;
    private LoanDAO loanDAO;
    private FineDAO fineDAO;
    private BookDAO bookDAO;
    private MemberDAO memberDAO;

    public ReturnFrame() {
        setTitle("Return Book");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loanDAO = new LoanDAO();
        fineDAO = new FineDAO();
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();

        // Panel utama dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Loan ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Loan ID:"), gbc);

        gbc.gridx = 1;
        loanIdComboBox = new JComboBox<>();
        loadLoanIds();
        formPanel.add(loanIdComboBox, gbc);

        // Member Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Member Name:"), gbc);

        gbc.gridx = 1;
        memberNameField = new JTextField(20);
        memberNameField.setEditable(false);
        formPanel.add(memberNameField, gbc);

        // Book Title
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Book Title:"), gbc);

        gbc.gridx = 1;
        bookTitleField = new JTextField(20);
        bookTitleField.setEditable(false);
        formPanel.add(bookTitleField, gbc);

        // Fine Amount
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Fine Amount (if any):"), gbc);

        gbc.gridx = 1;
        fineAmountField = new JTextField(20);
        fineAmountField.setEditable(false); // Denda dihitung otomatis
        formPanel.add(fineAmountField, gbc);

        // Return Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton returnButton = new JButton("Return Book");
        formPanel.add(returnButton, gbc);

        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);

        // Event Listeners
        loanIdComboBox.addActionListener(e -> updateLoanDetails());
        returnButton.addActionListener(e -> returnBook());

        setVisible(true);
    }

    private void loadLoanIds() {
        try {
            List<Loan> loans = loanDAO.getAll();
            for (Loan loan : loans) {
                if (loan.getReturnDate() == null) {
                    loanIdComboBox.addItem(loan.getId());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading loan IDs: " + e.getMessage());
        }
    }

    private void updateLoanDetails() {
        try {
            Integer loanId = (Integer) loanIdComboBox.getSelectedItem();
            if (loanId != null) {
                Loan loan = loanDAO.getById(loanId);
                if (loan != null) {
                    Member member = memberDAO.getById(loan.getMemberId());
                    Book book = bookDAO.getById(loan.getBookId());
                    if (member != null && book != null) {
                        memberNameField.setText(member.getName());
                        bookTitleField.setText(book.getName());
                        // Hitung denda saat ini (sebagai preview)
                        double fineAmount = calculateFine(loan);
                        fineAmountField.setText(fineAmount > 0 ? String.valueOf(fineAmount) : "0");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating loan details: " + e.getMessage());
        }
    }

    private double calculateFine(Loan loan) {
        Date dueDate = loan.getDueDate();
        Date returnDate = new Date(); // Tanggal pengembalian saat ini
        if (returnDate.before(dueDate) || returnDate.equals(dueDate)) {
            return 0; // Tidak ada denda jika belum melewati due date
        }

        long diffInMillies = returnDate.getTime() - dueDate.getTime();
        long daysLate = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (daysLate <= 0) {
            return 0; // Tidak ada denda jika belum terlambat
        }

        // Denda Rp 2.000 per hari terlambat
        return daysLate * 2000;
    }

    private void returnBook() {
        try {
            Integer loanId = (Integer) loanIdComboBox.getSelectedItem();
            if (loanId == null) {
                JOptionPane.showMessageDialog(this, "Please select a Loan ID!");
                return;
            }

            Loan loan = loanDAO.getById(loanId);
            if (loan == null) {
                JOptionPane.showMessageDialog(this, "Invalid Loan ID!");
                return;
            }

            if (loan.getReturnDate() != null) {
                JOptionPane.showMessageDialog(this, "This book has already been returned!");
                return;
            }

            // Set tanggal pengembalian
            Date returnDate = new Date();
            loan.setReturnDate(returnDate);
            loanDAO.update(loan);

            // Tambah stok setelah pengembalian
            bookDAO.increaseStock(loan.getBookId());

            // Hitung dan simpan denda
            double fineAmount = calculateFine(loan);
            if (fineAmount > 0) {
                Fine fine = new Fine(0, loanId, fineAmount, "UNPAID");
                fineDAO.save(fine);
                loan.setFine(fine);
                JOptionPane.showMessageDialog(this, "Book returned with fine: Rp " + fineAmount);
            } else {
                JOptionPane.showMessageDialog(this, "Book returned successfully! No fine incurred.");
            }

            // Perbarui dropdown
            loanIdComboBox.removeAllItems();
            loadLoanIds();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}