package views;

import models.DatabaseManager;
import models.Medication;
import models.User;
import views.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable medicationsTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private User currentUser;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private JCheckBox activeFilterCheckBox;
    private JCheckBox prescriptionFilterCheckBox;
    private TableRowSorter<DefaultTableModel> sorter;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton manageUsersButton;
    private JButton logoutButton;
    private JButton placeOrderButton;
    private JButton orderHistoryButton;

    public MainFrame(DatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;

        setTitle("Pharmacy Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadMedications();
        applyRoleRestrictions();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("Сортировка:"));
        sortComboBox = new JComboBox<>(new String[]{"Без сортировки", "Название", "Количество", "Цена"});
        searchPanel.add(sortComboBox);

        activeFilterCheckBox = new JCheckBox("Активные");
        prescriptionFilterCheckBox = new JCheckBox("Требуется рецепт");
        searchPanel.add(activeFilterCheckBox);
        searchPanel.add(prescriptionFilterCheckBox);

        panel.add(searchPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        manageUsersButton = new JButton("Управление пользователями");
        logoutButton = new JButton("Выход");
        placeOrderButton = new JButton("Оформить заказ");
        orderHistoryButton = new JButton("История заказов");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        if ("admin".equals(currentUser.getRole())) {
            buttonPanel.add(manageUsersButton);
        }
        buttonPanel.add(placeOrderButton);
        buttonPanel.add(orderHistoryButton);
        buttonPanel.add(logoutButton);

        String[] columnNames = {"ID", "Название", "Описание", "Количество", "Штрих-код", "Активно", "Рецепт", "Цена"};
        tableModel = new DefaultTableModel(columnNames, 0);
        medicationsTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        medicationsTable.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(medicationsTable);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            if (canEditMedications()) {
                openMedicationForm(null);
            } else {
                showAccessDeniedMessage();
            }
        });

        editButton.addActionListener(e -> {
            if (canEditMedications()) {
                int selectedRow = medicationsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    Medication medication = dbManager.getMedicationById(id);
                    openMedicationForm(medication);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Выберите лекарство для редактирования.");
                }
            } else {
                showAccessDeniedMessage();
            }
        });

        deleteButton.addActionListener(e -> {
            if (canEditMedications()) {
                int selectedRow = medicationsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    dbManager.softDeleteMedication(id, currentUser);
                    loadMedications();
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Выберите лекарство для удаления.");
                }
            } else {
                showAccessDeniedMessage();
            }
        });

        manageUsersButton.addActionListener(e -> {
            if ("admin".equals(currentUser.getRole())) {
                UserManagementFrame userManagementFrame = new UserManagementFrame(dbManager);
                userManagementFrame.setVisible(true);
            } else {
                showAccessDeniedMessage();
            }
        });

        logoutButton.addActionListener(e -> logout());

        placeOrderButton.addActionListener(e -> openOrderFrame());

        orderHistoryButton.addActionListener(e -> openOrderHistoryFrame());

        searchField.addActionListener(e -> applyFilters());

        sortComboBox.addActionListener(e -> applySort());

        activeFilterCheckBox.addActionListener(e -> applyFilters());

        prescriptionFilterCheckBox.addActionListener(e -> applyFilters());
    }

    void loadMedications() {
        tableModel.setRowCount(0);
        List<Medication> medications = dbManager.getAllMedications();
        for (Medication medication : medications) {
            tableModel.addRow(new Object[]{
                    medication.getId(),
                    medication.getName(),
                    medication.getDescription(),
                    medication.getQuantity(),
                    medication.getBarcode(),
                    medication.isActive(),
                    medication.isPrescriptionRequired(),
                    medication.getPrice()
            });
        }
    }

    private void openMedicationForm(Medication medication) {
        MedicationForm form = new MedicationForm(this, dbManager, medication, currentUser);
        form.setVisible(true);
        loadMedications();
    }

    private void openOrderFrame() {
        if ("admin".equals(currentUser.getRole()) || "pharmacist".equals(currentUser.getRole())) {
            OrderFrame orderFrame = new OrderFrame(dbManager, currentUser, this);
            orderFrame.setVisible(true);
        } else {
            showAccessDeniedMessage();
        }
    }

    private boolean canEditMedications() {
        return "admin".equals(currentUser.getRole()) || "pharmacist".equals(currentUser.getRole());
    }

    private void showAccessDeniedMessage() {
        JOptionPane.showMessageDialog(this, "У вас нет прав на выполнение этого действия.", "Доступ запрещен", JOptionPane.WARNING_MESSAGE);
    }

    private void applyRoleRestrictions() {
        if ("trainee".equals(currentUser.getRole())) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            placeOrderButton.setEnabled(false);
        } else if ("manager".equals(currentUser.getRole())) {
            manageUsersButton.setEnabled(false);
        }
    }

    private void logout() {
        dispose();
        new LoginFrame(dbManager).setVisible(true);
    }

    private void openOrderHistoryFrame() {
        if ("admin".equals(currentUser.getRole()) || "manager".equals(currentUser.getRole())) {
            OrderHistoryFrame orderHistoryFrame = new OrderHistoryFrame(dbManager);
            orderHistoryFrame.setVisible(true);
        } else {
            showAccessDeniedMessage();
        }
    }

    private void applyFilters() {
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String nameOrBarcode = searchField.getText().toLowerCase();
                boolean activeFilter = activeFilterCheckBox.isSelected();
                boolean prescriptionFilter = prescriptionFilterCheckBox.isSelected();

                String name = entry.getStringValue(1).toLowerCase();
                String barcode = entry.getStringValue(4).toLowerCase();
                boolean isActive = (Boolean) entry.getValue(5);
                boolean requiresPrescription = (Boolean) entry.getValue(6);

                boolean matchesSearch = name.contains(nameOrBarcode) || barcode.contains(nameOrBarcode);
                boolean matchesActive = !activeFilter || isActive;
                boolean matchesPrescription = !prescriptionFilter || requiresPrescription;

                return matchesSearch && matchesActive && matchesPrescription;
            }
        };
        sorter.setRowFilter(rf);
    }

    private void applySort() {
        int sortIndex = sortComboBox.getSelectedIndex();
        sorter.setSortKeys(null);

        if (sortIndex == 1) {
            sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
        } else if (sortIndex == 2) {
            sorter.setSortKeys(List.of(new RowSorter.SortKey(3, SortOrder.ASCENDING)));
        } else if (sortIndex == 3) {
            sorter.setSortKeys(List.of(new RowSorter.SortKey(7, SortOrder.ASCENDING)));
        }
    }
}
