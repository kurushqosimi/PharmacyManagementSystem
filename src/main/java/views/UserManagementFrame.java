package views;

import models.DatabaseManager;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementFrame extends JFrame {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;

    public UserManagementFrame(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        setTitle("Управление пользователями");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadUsers();
    }

    private void initUI() {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Добавить пользователя");
        JButton editButton = new JButton("Редактировать пользователя");
        JButton deactivateButton = new JButton("Деактивировать пользователя");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deactivateButton);

        String[] columnNames = {"ID", "Имя пользователя", "Роль", "Активен"};
        tableModel = new DefaultTableModel(columnNames, 0);
        usersTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(usersTable);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserForm(null);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = usersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = (int) tableModel.getValueAt(selectedRow, 0);
                    User user = dbManager.getUserById(userId);
                    openUserForm(user);
                } else {
                    JOptionPane.showMessageDialog(UserManagementFrame.this, "Выберите пользователя для редактирования.");
                }
            }
        });

        deactivateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = usersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = (int) tableModel.getValueAt(selectedRow, 0);
                    dbManager.deactivateUser(userId);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(UserManagementFrame.this, "Выберите пользователя для деактивации.");
                }
            }
        });
    }

    private void loadUsers() {
        List<User> users = dbManager.getAllUsers();
        tableModel.setRowCount(0); // Очистка таблицы
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.isActive() ? "Да" : "Нет"
            });
        }
    }

    private void openUserForm(User user) {
        UserForm userForm = new UserForm(this, dbManager, user);
        userForm.setVisible(true);
        loadUsers();
    }
}
