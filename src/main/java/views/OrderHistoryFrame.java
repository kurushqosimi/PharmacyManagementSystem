package views;

import models.DatabaseManager;
import models.OrderHistory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OrderHistoryFrame extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private JButton viewDetailsButton; // Кнопка для просмотра деталей заказа

    public OrderHistoryFrame(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        setTitle("История заказов");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadOrderHistory(dbManager.getOrderHistory()); // Загрузка всей истории заказов при открытии
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица для отображения заказов
        String[] columnNames = {"ID заказа", "Дата", "Сумма", "Пользователь"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопкой
        JPanel buttonPanel = new JPanel();
        viewDetailsButton = new JButton("Просмотр деталей заказа");
        viewDetailsButton.setEnabled(false); // По умолчанию отключена
        buttonPanel.add(viewDetailsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Обработчик для кнопки "Просмотр деталей заказа"
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) { // Проверяем, что строка выбрана
                    int orderId = (int) tableModel.getValueAt(selectedRow, 0);
                    openOrderDetails(orderId);
                }
            }
        });

        // Обработчик выбора строки в таблице для активации кнопки
        orderTable.getSelectionModel().addListSelectionListener(event -> {
            boolean isRowSelected = orderTable.getSelectedRow() != -1;
            viewDetailsButton.setEnabled(isRowSelected); // Включаем кнопку, если строка выбрана
        });
    }

    private void loadOrderHistory(List<OrderHistory> orders) {
        tableModel.setRowCount(0); // Очистка таблицы
        for (OrderHistory order : orders) {
            tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getCreatedByName() // Используем имя пользователя
            });
        }
    }

    private void openOrderDetails(int orderId) {
        OrderDetailFrame detailFrame = new OrderDetailFrame(dbManager, orderId);
        detailFrame.setVisible(true);
    }
}
