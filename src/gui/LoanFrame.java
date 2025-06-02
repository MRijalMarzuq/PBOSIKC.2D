package gui;

import dao.BookDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import model.Book;
import model.Loan;
import model.Member;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LoanFrame extends JFrame {
    private JComboBox<Integer> memberIdComboBox, bookIdComboBox;
    private JTextField memberNameField, bookTitleField, stockField;
    private LoanDAO loanDAO;
    private MemberDAO memberDAO;
    private BookDAO bookDAO;

    public LoanFrame() {
        setTitle("Loan Book");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loanDAO = new LoanDAO();
        memberDAO = new MemberDAO();
        bookDAO = new BookDAO();

        // Panel utama dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Member ID (Dropdown)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Member ID:"), gbc);

        gbc.gridx = 1;
        memberIdComboBox = new JComboBox<>();
        loadMemberIds();
        formPanel.add(memberIdComboBox, gbc);

        // Member Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Member Name:"), gbc);

        gbc.gridx = 1;
        memberNameField = new JTextField(20);
        memberNameField.setEditable(false);
        formPanel.add(memberNameField, gbc);

        // Book ID (Dropdown)
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Book ID:"), gbc);

        gbc.gridx = 1;
        bookIdComboBox = new JComboBox<>();
        loadBookIds();
        formPanel.add(bookIdComboBox, gbc);

        // Book Title
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Book Title:"), gbc);

        gbc.gridx = 1;
        bookTitleField = new JTextField(20);
        bookTitleField.setEditable(false);
        formPanel.add(bookTitleField, gbc);

        // Stock
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Stock Available:"), gbc);

        gbc.gridx = 1;
        stockField = new JTextField(20);
        stockField.setEditable(false);
        formPanel.add(stockField, gbc);

        // Loan Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loanButton = new JButton("Loan Book");
        formPanel.add(loanButton, gbc);

        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);

        // Event Listeners
        memberIdComboBox.addActionListener(e -> updateMemberDetails());
        bookIdComboBox.addActionListener(e -> updateBookDetails());
        loanButton.addActionListener(e -> loanBook());

        setVisible(true);
    }

    private void loadMemberIds() {
        try {
            List<Member> members = memberDAO.getAll();
            if (members.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No members found! Please add members first.");
            } else {
                for (Member member : members) {
                    memberIdComboBox.addItem(member.getId());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading member IDs: " + e.getMessage());
        }
    }

    private void loadBookIds() {
        try {
            List<Book> books = bookDAO.getAll();
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books found! Please add books first.");
            } else {
                for (Book book : books) {
                    bookIdComboBox.addItem(book.getId());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading book IDs: " + e.getMessage());
        }
    }

    private void updateMemberDetails() {
        try {
            Integer memberId = (Integer) memberIdComboBox.getSelectedItem();
            if (memberId != null) {
                Member member = memberDAO.getById(memberId);
                if (member != null) {
                    memberNameField.setText(member.getName());
                } else {
                    memberNameField.setText("");
                    JOptionPane.showMessageDialog(this, "Member not found!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching member: " + e.getMessage());
        }
    }

    private void updateBookDetails() {
        try {
            Integer bookId = (Integer) bookIdComboBox.getSelectedItem();
            if (bookId != null) {
                Book book = bookDAO.getById(bookId);
                if (book != null) {
                    bookTitleField.setText(book.getName());
                    stockField.setText(String.valueOf(book.getStock()));
                } else {
                    bookTitleField.setText("");
                    stockField.setText("");
                    JOptionPane.showMessageDialog(this, "Book not found!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching book: " + e.getMessage());
        }
    }

    private void loanBook() {
        try {
            Integer memberId = (Integer) memberIdComboBox.getSelectedItem();
            Integer bookId = (Integer) bookIdComboBox.getSelectedItem();

            if (memberId == null || bookId == null) {
                JOptionPane.showMessageDialog(this, "Please select a Member ID and Book ID!");
                return;
            }

            Member member = memberDAO.getById(memberId);
            Book book = bookDAO.getById(bookId);

            if (member == null) {
                JOptionPane.showMessageDialog(this, "Member not found!");
                return;
            }
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book not found!");
                return;
            }

            if (book.getStock() <= 0) {
                JOptionPane.showMessageDialog(this, "Book out of stock!");
                return;
            }

            // Buat peminjaman baru
            Date loanDate = new Date(); // Tanggal saat ini: 29 Mei 2025, 22:12 WIB
            Loan loan = new Loan(0, memberId, bookId, loanDate);
            loanDAO.save(loan);

            // Kurangi stok buku
            bookDAO.decreaseStock(bookId);

            JOptionPane.showMessageDialog(this, "Book loaned successfully! Due date: " + loan.getDueDate());
            clearFields();
            // Perbarui dropdown
            loadBookIds();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating loan: " + e.getMessage());
        }
    }

    private void clearFields() {
        memberIdComboBox.setSelectedIndex(-1);
        bookIdComboBox.setSelectedIndex(-1);
        memberNameField.setText("");
        bookTitleField.setText("");
        stockField.setText("");
    }
}