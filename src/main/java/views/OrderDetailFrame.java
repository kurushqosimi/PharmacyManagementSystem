package views;

import models.DatabaseManager;
import models.OrderDetail;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderDetailFrame extends JFrame {
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private int orderId;

    public OrderDetailFrame(DatabaseManager dbManager, int orderId) {
        this.dbManager = dbManager;
        this.orderId = orderId;

        setTitle("Детали заказа #" + orderId);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadOrderDetails();
    }

    private void initUI() {
        String[] columnNames = {"Название", "Количество", "Цена за единицу", "Сумма"};
        tableModel = new DefaultTableModel(columnNames, 0);
        detailTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(detailTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadOrderDetails() {
        List<OrderDetail> orderDetails = dbManager.getOrderDetails(orderId);
        tableModel.setRowCount(0);
        for (OrderDetail detail : orderDetails) {
            tableModel.addRow(new Object[]{
                    detail.getMedicationName(),
                    detail.getQuantity(),
                    detail.getPricePerUnit(),
                    detail.getPricePerUnit() * detail.getQuantity()
            });
        }
    }
}
