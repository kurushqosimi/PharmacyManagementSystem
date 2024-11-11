package views;

import models.DatabaseManager;
import models.Medication;
import models.Order;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderFrame extends JFrame {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private Order currentOrder;
    private DatabaseManager dbManager;
    private User currentUser;
    private MainFrame mainFrame; // Добавляем ссылку на MainFrame

    public OrderFrame(DatabaseManager dbManager, User currentUser, MainFrame mainFrame) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;
        this.mainFrame = mainFrame; // Инициализируем ссылку на MainFrame
        this.currentOrder = new Order();

        setTitle("Оформление заказа");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Название", "Количество", "Цена за единицу", "Сумма"};
        tableModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить товар");
        JButton completeOrderButton = new JButton("Завершить заказ");

        buttonPanel.add(addButton);
        buttonPanel.add(completeOrderButton);

        JPanel totalPanel = new JPanel();
        totalLabel = new JLabel("Общая сумма: 0.0");
        totalPanel.add(totalLabel);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(totalPanel, BorderLayout.NORTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicationToOrder();
            }
        });

        completeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeOrder();
            }
        });
    }

    private void addMedicationToOrder() {
        String barcode = JOptionPane.showInputDialog(this, "Введите штрих-код товара:");
        Medication medication = dbManager.getMedicationByBarcode(barcode);

        if (medication != null) {
            int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Введите количество:"));

            boolean found = false;
            for (Medication item : currentOrder.getMedications()) {
                if (item.getId() == medication.getId()) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                currentOrder.addMedication(medication, quantity);
            }

            updateCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Лекарство не найдено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);
        for (Medication medication : currentOrder.getMedications()) {
            tableModel.addRow(new Object[]{
                    medication.getName(),
                    medication.getQuantity(),
                    medication.getPrice(),
                    medication.getPrice() * medication.getQuantity()
            });
        }
        currentOrder.calculateTotalAmount();
        totalLabel.setText("Общая сумма: " + currentOrder.getTotalAmount());
    }

    private void completeOrder() {
        if (currentOrder.getMedications().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Корзина пуста. Пожалуйста, добавьте товары перед завершением заказа.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dbManager.completeOrder(currentOrder, currentUser);
        JOptionPane.showMessageDialog(this, "Заказ успешно завершен. Общая сумма: " + currentOrder.getTotalAmount());

        // Обновляем таблицу лекарств в MainFrame после завершения заказа
        if (mainFrame != null) {
            mainFrame.loadMedications();
        }

        dispose();
    }
}
