package gui;

import dao.CategoryDAO;
import model.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoryFrame extends JFrame {
    private JTextField nameField;
    private DefaultTableModel tableModel;
    private JTable categoryTable;
    private CategoryDAO categoryDAO;
    private int selectedCategoryId = -1;

    public CategoryFrame() {
        setTitle("Manage Categories");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        categoryDAO = new CategoryDAO();

        // Form Panel dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label dan Field untuk Category Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Category Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Tombol Add Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JButton addButton = new JButton("Add Category");
        formPanel.add(addButton, gbc);

        // Tombol Delete Category
        gbc.gridx = 1;
        gbc.gridy = 1;
        JButton deleteButton = new JButton("Delete Category");
        formPanel.add(deleteButton, gbc);

        // Tabel Kategori
        tableModel = new DefaultTableModel(new String[]{"ID", "Name"}, 0);
        categoryTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(categoryTable);

        // Mouse Listener untuk memilih baris di tabel
        categoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = categoryTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    selectedCategoryId = (int) tableModel.getValueAt(row, 0);
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                }
            }
        });

        // Layout Utama
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Event Listeners
        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());

        // Load data awal
        loadCategories();
        setVisible(true);
    }

    private void addCategory() {
        String categoryName = nameField.getText().trim();
        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Category category = new Category(0, categoryName);
            categoryDAO.save(category);
            JOptionPane.showMessageDialog(this, "Category added successfully!");
            loadCategories();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategory() {
        if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryDAO.delete(selectedCategoryId);
                JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                loadCategories();
                clearFields();
                selectedCategoryId = -1;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting category: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAll();
            tableModel.setRowCount(0);
            for (Category category : categories) {
                tableModel.addRow(new Object[]{category.getId(), category.getName()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
    }
}