package gui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("Dashboard Perdi");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gunakan GridLayout dengan 1 baris dan 3 kolom utama
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel Kolom 1 - Buku
        JPanel booksPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton categoryButton = new JButton("Kelola Categories");
        JButton bookButton = new JButton("Kelola Buku");
        JButton bookViewButton = new JButton("Lihat Buku");
        JButton searchButton = new JButton("Cari Buku");
        booksPanel.add(bookButton);
        booksPanel.add(bookViewButton);
        booksPanel.add(searchButton);
        booksPanel.add(categoryButton);

        // Panel Kolom 2 - Anggota
        JPanel membersPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton memberButton = new JButton("Kelola Anggota");
        JButton memberProfileButton = new JButton("Profil Anggota");
        JButton fineButton = new JButton("Kelola Denda");
        membersPanel.add(memberButton);
        membersPanel.add(memberProfileButton);
        membersPanel.add(fineButton);

        // Panel Kolom 3 - Peminjaman
        JPanel loansPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton loanButton = new JButton("Peminjaman Buku");
        JButton returnButton = new JButton("Pengembalian Buku");
        JButton reportButton = new JButton("Laporan Peminjaman");
        JButton popularButton = new JButton("Laporan Buku Terpopuler");
        loansPanel.add(loanButton);
        loansPanel.add(returnButton);
        loansPanel.add(reportButton);
        loansPanel.add(popularButton);

        // Tambahkan setiap panel ke dalam mainPanel
        mainPanel.add(booksPanel);
        mainPanel.add(membersPanel);
        mainPanel.add(loansPanel);

        // Tambahkan ActionListener (opsional, tidak dihapus)
        bookButton.addActionListener(e -> new BookManagementFrame().setVisible(true));
        bookViewButton.addActionListener(e -> new BookViewFrame().setVisible(true));
        searchButton.addActionListener(e -> new SearchFrame().setVisible(true));
        categoryButton.addActionListener(e -> new CategoryFrame().setVisible(true));

        memberButton.addActionListener(e -> new MemberManagementFrame().setVisible(true));
        memberProfileButton.addActionListener(e -> new MemberProfileFrame().setVisible(true));
        fineButton.addActionListener(e -> new ManageFineFrame().setVisible(true));

        loanButton.addActionListener(e -> new LoanFrame().setVisible(true));
        returnButton.addActionListener(e -> new ReturnFrame().setVisible(true));
        reportButton.addActionListener(e -> new LoanViewFrame().setVisible(true));
        popularButton.addActionListener(e -> new PopularBooksFrame().setVisible(true));

        // Tambahkan main panel ke frame
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
