package models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private List<Medication> medications;
    private double totalAmount;

    public Order() {
        this.medications = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    // Добавление лекарства в заказ
    public void addMedication(Medication medication, int quantity) {
        medication.setQuantity(quantity);
        medications.add(medication);
        totalAmount += medication.getPrice() * quantity;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    // Метод для вычисления общей суммы
    public void calculateTotalAmount() {
        totalAmount = medications.stream().mapToDouble(m -> m.getPrice() * m.getQuantity()).sum();
    }
}
