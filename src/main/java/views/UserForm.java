package views;

import models.DatabaseManager;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserForm extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JCheckBox isActiveCheckBox;
    private DatabaseManager dbManager;
    private User user;

    public UserForm(JFrame parent, DatabaseManager dbManager, User user) {
        super(parent, true);
        this.dbManager = dbManager;
        this.user = user;

        setTitle(user == null ? "Добавить пользователя" : "Редактировать пользователя");
        setSize(400, 300);
        setLocationRelativeTo(parent);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        panel.add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Роль:"));
        roleComboBox = new JComboBox<>(new String[]{"admin", "pharmacist", "trainee", "manager"});
        panel.add(roleComboBox);

        panel.add(new JLabel("Активен:"));
        isActiveCheckBox = new JCheckBox();
        panel.add(isActiveCheckBox);

        JButton saveButton = new JButton("Сохранить");
        panel.add(saveButton);

        add(panel, BorderLayout.CENTER);

        if (user != null) {
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword()); // Хранение пароля в открытом виде не рекомендуется
            roleComboBox.setSelectedItem(user.getRole());
            isActiveCheckBox.setSelected(user.isActive());
        }

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUser();
            }
        });
    }

    private void saveUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        boolean isActive = isActiveCheckBox.isSelected();

        if (user == null) {
            User newUser = new User(0, username, password, role, isActive);
            dbManager.addUser(newUser);
        } else {
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setActive(isActive);
            dbManager.updateUser(user);
        }
        dispose();
    }
}
