package views;

import models.DatabaseManager;
import models.Medication;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MedicationForm extends JDialog {
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField quantityField;
    private JTextField barcodeField;
    private JTextField priceField;
    private JCheckBox isActiveCheckBox;
    private JCheckBox isPrescriptionRequiredCheckBox;
    private JButton saveButton;

    private DatabaseManager dbManager;
    private Medication medication;
    private User currentUser;

    public MedicationForm(JFrame parent, DatabaseManager dbManager, Medication medication, User currentUser) {
        super(parent, true);
        this.dbManager = dbManager;
        this.medication = medication;
        this.currentUser = currentUser;

        setTitle(medication == null ? "Добавить Лекарство" : "Редактировать Лекарство");
        setSize(400, 300);
        setLocationRelativeTo(parent);

        initUI();
        applyAccessRestrictions();  // Применение ограничений на основе роли пользователя
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        panel.add(new JLabel("Название:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Описание:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel("Количество:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        panel.add(new JLabel("Штрих-код:"));
        barcodeField = new JTextField();
        panel.add(barcodeField);

        panel.add(new JLabel("Цена:")); // Поле для ввода цены
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Активно:"));
        isActiveCheckBox = new JCheckBox();
        panel.add(isActiveCheckBox);

        panel.add(new JLabel("Рецепт:"));
        isPrescriptionRequiredCheckBox = new JCheckBox();
        panel.add(isPrescriptionRequiredCheckBox);

        saveButton = new JButton("Сохранить");
        panel.add(saveButton);

        add(panel, BorderLayout.CENTER);

        if (medication != null) {
            // Если запись уже существует, заполняем поля данными
            nameField.setText(medication.getName());
            descriptionField.setText(medication.getDescription());
            quantityField.setText(String.valueOf(medication.getQuantity()));
            barcodeField.setText(medication.getBarcode());
            priceField.setText(String.valueOf(medication.getPrice()));
            isActiveCheckBox.setSelected(medication.isActive());
            isPrescriptionRequiredCheckBox.setSelected(medication.isPrescriptionRequired());
        }

        // Обработчик события сохранения
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMedication();
            }
        });
    }

    private void saveMedication() {
        // Проверка на возможность сохранения изменений (для пользователей с правом редактирования)
        if (!canEditMedication()) {
            JOptionPane.showMessageDialog(this, "У вас нет прав на редактирование или добавление лекарства.", "Доступ запрещен", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Чтение данных из полей формы
        String name = nameField.getText();
        String description = descriptionField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        String barcode = barcodeField.getText();
        boolean isActive = isActiveCheckBox.isSelected();
        boolean isPrescriptionRequired = isPrescriptionRequiredCheckBox.isSelected();
        Double price = Double.parseDouble(priceField.getText());

        // В зависимости от наличия записи, либо добавляем новое лекарство, либо обновляем существующее
        if (medication == null) {  // добавление нового лекарства
            Medication newMedication = new Medication(0, name, description, quantity, barcode, isActive, isPrescriptionRequired, currentUser.getId(), price);
            dbManager.addMedication(newMedication, currentUser);
        } else {  // обновление существующего лекарства
            medication.setName(name);
            medication.setDescription(description);
            medication.setQuantity(quantity);
            medication.setBarcode(barcode);
            medication.setActive(isActive);
            medication.setPrescriptionRequired(isPrescriptionRequired);
            medication.setPrice(price);
            dbManager.updateMedication(medication, currentUser);
        }
        dispose();
    }

    private boolean canEditMedication() {
        // Проверка прав на основе роли текущего пользователя
        return "admin".equals(currentUser.getRole()) || "pharmacist".equals(currentUser.getRole());
    }

    private void applyAccessRestrictions() {
        if (!canEditMedication()) {
            // Отключаем возможность редактирования для пользователей без прав на добавление/изменение
            nameField.setEnabled(false);
            descriptionField.setEnabled(false);
            quantityField.setEnabled(false);
            barcodeField.setEnabled(false);
            priceField.setEnabled(false);
            isActiveCheckBox.setEnabled(false);
            isPrescriptionRequiredCheckBox.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }
}
