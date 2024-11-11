package views;

import models.DatabaseManager;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DatabaseManager dbManager;

    public LoginFrame(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setTitle("Вход в систему");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Войти");
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = dbManager.authenticateUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Добро пожаловать, " + user.getUsername());
            dispose(); // Закрытие окна входа
            MainFrame mainFrame = new MainFrame(dbManager, user);
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Неверное имя пользователя или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
