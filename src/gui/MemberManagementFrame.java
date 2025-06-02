package gui;

import dao.MemberDAO;
import model.Member;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class MemberManagementFrame extends JFrame {
    private JTextField nameField, emailField, searchEmailField;
    private DefaultTableModel tableModel;
    private JTable memberTable;
    private MemberDAO memberDAO;
    private int selectedMemberId = -1;

    public MemberManagementFrame() {
        setTitle("Manage Members");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        memberDAO = new MemberDAO();

        // Panel utama dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Jarak antar elemen
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Tombol Add Member
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JButton addButton = new JButton("Add Member");
        formPanel.add(addButton, gbc);

        // Tombol Update Member
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JButton updateButton = new JButton("Update Member");
        formPanel.add(updateButton, gbc);

        // Tombol Delete Member
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JButton deleteButton = new JButton("Delete Member");
        formPanel.add(deleteButton, gbc);

        // Search Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Search Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        searchEmailField = new JTextField(15);
        formPanel.add(searchEmailField, gbc);

        // Tombol Search
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JButton searchButton = new JButton("Search");
        formPanel.add(searchButton, gbc);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0);
        memberTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(memberTable);

        // Tambahkan MouseListener untuk klik pada baris tabel
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = memberTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    selectedMemberId = (int) tableModel.getValueAt(row, 0);
                    try {
                        Member member = memberDAO.getById(selectedMemberId);
                        if (member != null) {
                            nameField.setText(member.getName());
                            emailField.setText(member.getEmail());
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(MemberManagementFrame.this,
                                "Error retrieving member details: " + ex.getMessage());
                    }
                }
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Event Listeners
        searchButton.addActionListener(e -> searchMembers());
        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());
        loadMembers();

        setVisible(true);
    }

    private void searchMembers() {
        try {
            String email = searchEmailField.getText().trim();
            List<Member> members;
            if (email.isEmpty()) {
                members = memberDAO.getAll();
            } else {
                members = memberDAO.searchByEmail(email);
            }
            tableModel.setRowCount(0);
            for (Member member : members) {
                tableModel.addRow(new Object[]{member.getId(), member.getName(), member.getEmail()});
            }
            if (members.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No members found with email containing: " + email);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching members: " + e.getMessage());
        }
    }

    private void addMember() {
        try {
            Member member = new Member(0, nameField.getText(), emailField.getText());
            memberDAO.save(member);
            JOptionPane.showMessageDialog(this, "Member added successfully!");
            clearForm();
            loadMembers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to update!");
            return;
        }
        try {
            Member member = new Member(selectedMemberId, nameField.getText(), emailField.getText());
            memberDAO.update(member);
            JOptionPane.showMessageDialog(this, "Member updated successfully!");
            clearForm();
            loadMembers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to delete!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                memberDAO.delete(selectedMemberId);
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                clearForm();
                loadMembers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadMembers() {
        try {
            List<Member> members = memberDAO.getAll();
            tableModel.setRowCount(0);
            for (Member member : members) {
                tableModel.addRow(new Object[]{member.getId(), member.getName(), member.getEmail()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        searchEmailField.setText("");
        selectedMemberId = -1;
    }
}